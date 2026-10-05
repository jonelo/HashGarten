/*

  HashGarten 0.21.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2026 Dipl.-Inf. (FH) Johann N. Löfflmann,
  All Rights Reserved, <https://jacksum.net>.

  This program is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  This program is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
  details.

  You should have received a copy of the GNU General Public License along with
  this program. If not, see <https://www.gnu.org/licenses/>.

 */
package net.jacksum.gui.util;

import com.formdev.flatlaf.util.SystemInfo;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import net.jacksum.gui.GUIHelper;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BOOLEAN;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

/**
 * Adjusts the native macOS menu bar and application menu (About, Settings, Quit), which are built
 * by the JDK and which Java offers no API for. It talks to the Objective-C runtime through the
 * Foreign Function and Memory API.
 *
 * AppKit objects may only be changed on the main thread, which is not the EDT, so every change is
 * sent there with performSelectorOnMainThread. Reading is done on the calling thread.
 *
 * None of the methods throws: the menu bar is cosmetics, a failure is logged only.
 */
public final class MacAppMenu {

    // the tag of the Settings item in the JDK's default NIB, see PREFERENCES_TAG in ApplicationDelegate.m
    private static final long PREFERENCES_TAG = 42;

    private MacAppMenu() {
    }

    /**
     * Removes the image of the Settings item in the application menu on macOS 26 and later.
     *
     * The item in the JDK's default NIB carries an image. macOS 26 reserves room for it, but does
     * not draw it, because the java launcher has been linked against an older macOS SDK (14.2 for
     * Temurin 25), and macOS draws menu symbols only for applications that have been linked against
     * a recent SDK (that is also why the shortcuts lack their modifier symbols). So "Settings…" was
     * indented compared to the other items, without showing an icon. Without the image it is
     * aligned with them.
     */
    public static void removeSettingsImage() {
        if (!SystemInfo.isMacOS || SystemInfo.osVersion < SystemInfo.toVersion(26, 0, 0, 0)) {
            return;
        }
        try (Arena arena = Arena.ofConfined()) {
            ObjC objc = new ObjC(arena);
            MemorySegment mainMenu = objc.mainMenu();
            if (mainMenu.address() == 0) {
                return;
            }
            MemorySegment appMenuItem = objc.sendLong(mainMenu, "itemAtIndex:", 0L);
            if (appMenuItem.address() == 0) {
                return;
            }
            MemorySegment appMenu = objc.send(appMenuItem, "submenu");
            if (appMenu.address() == 0) {
                return;
            }
            MemorySegment settingsItem = objc.sendLong(appMenu, "itemWithTag:", PREFERENCES_TAG);
            if (settingsItem.address() == 0) {
                return;
            }
            objc.performOnMainThread(settingsItem, "setImage:", MemorySegment.NULL);
        } catch (Throwable t) {
            GUIHelper.debug(String.format("Could not adjust the Settings item of the application menu: %s", t));
        }
    }

    /**
     * Returns the number of items in the native main menu, i.e. the application menu plus the menus
     * of the screen menu bar that have arrived there so far.
     *
     * @return the number of items, or -1 if it is unknown
     */
    public static long mainMenuItemCount() {
        if (!SystemInfo.isMacOS) {
            return -1;
        }
        try (Arena arena = Arena.ofConfined()) {
            ObjC objc = new ObjC(arena);
            MemorySegment mainMenu = objc.mainMenu();
            return mainMenu.address() == 0 ? -1 : objc.sendReturningLong(mainMenu, "numberOfItems");
        } catch (Throwable t) {
            GUIHelper.debug(String.format("Could not count the items of the main menu: %s", t));
            return -1;
        }
    }

    /**
     * Makes AppKit draw the menu bar again, by setting the main menu to nil and back.
     *
     * When the frame is activated, the JDK puts the menus of the screen menu bar into the native main
     * menu, but sometimes macOS does not redraw the menu bar afterwards: only the application menu is
     * shown, and "Operating Mode" and "Help" appear only when the mouse is moved over the menu bar.
     * It depends on the timing, so it hardly ever happens on a warm start, but mostly on a slow one
     * (a cold start after a build; -Xint -Xshare:off reproduces it in 7 of 9 starts). Setting the same
     * main menu again does not help, AppKit ignores it; setting nil first does (27 of 27 starts).
     */
    public static void redrawMenuBar() {
        if (!SystemInfo.isMacOS) {
            return;
        }
        try (Arena arena = Arena.ofConfined()) {
            ObjC objc = new ObjC(arena);
            MemorySegment mainMenu = objc.mainMenu();
            if (mainMenu.address() == 0) {
                return;
            }
            // both calls are queued on the main thread in this order; the menu is retained until then
            objc.performOnMainThread(objc.nsApp(), "setMainMenu:", MemorySegment.NULL);
            objc.performOnMainThread(objc.nsApp(), "setMainMenu:", mainMenu);
        } catch (Throwable t) {
            GUIHelper.debug(String.format("Could not redraw the menu bar: %s", t));
        }
    }

    /**
     * The few calls into the Objective-C runtime that are needed here. objc_msgSend has to be called
     * with the exact prototype of each method, so there is one handle per prototype.
     */
    private static final class ObjC {

        private static final Linker LINKER = Linker.nativeLinker();
        private static final SymbolLookup LIBOBJC =
                SymbolLookup.libraryLookup("/usr/lib/libobjc.A.dylib", Arena.global());
        private static final MemorySegment MSG_SEND = LIBOBJC.find("objc_msgSend").orElseThrow();

        private static final MethodHandle GET_CLASS = LINKER.downcallHandle(
                LIBOBJC.find("objc_getClass").orElseThrow(), FunctionDescriptor.of(ADDRESS, ADDRESS));
        private static final MethodHandle SEL_REGISTER_NAME = LINKER.downcallHandle(
                LIBOBJC.find("sel_registerName").orElseThrow(), FunctionDescriptor.of(ADDRESS, ADDRESS));
        // id (id, SEL)
        private static final MethodHandle SEND = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
        // id (id, SEL, NSInteger)
        private static final MethodHandle SEND_LONG = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG));
        // NSInteger (id, SEL)
        private static final MethodHandle SEND_RETURNING_LONG = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(JAVA_LONG, ADDRESS, ADDRESS));
        // void (id, SEL, SEL, id, BOOL), i.e. performSelectorOnMainThread:withObject:waitUntilDone:
        private static final MethodHandle SEND_PERFORM = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS, ADDRESS, JAVA_BOOLEAN));

        private final Arena arena;

        ObjC(Arena arena) {
            this.arena = arena;
        }

        MemorySegment selector(String name) throws Throwable {
            return (MemorySegment) SEL_REGISTER_NAME.invokeExact(arena.allocateFrom(name));
        }

        MemorySegment send(MemorySegment receiver, String selector) throws Throwable {
            return (MemorySegment) SEND.invokeExact(receiver, selector(selector));
        }

        MemorySegment sendLong(MemorySegment receiver, String selector, long argument) throws Throwable {
            return (MemorySegment) SEND_LONG.invokeExact(receiver, selector(selector), argument);
        }

        long sendReturningLong(MemorySegment receiver, String selector) throws Throwable {
            return (long) SEND_RETURNING_LONG.invokeExact(receiver, selector(selector));
        }

        // asynchronously, so that it can never block the EDT while the main thread waits for it
        void performOnMainThread(MemorySegment receiver, String selector, MemorySegment argument) throws Throwable {
            SEND_PERFORM.invokeExact(receiver, selector("performSelectorOnMainThread:withObject:waitUntilDone:"),
                    selector(selector), argument, false);
        }

        MemorySegment nsApp() throws Throwable {
            return send((MemorySegment) GET_CLASS.invokeExact(arena.allocateFrom("NSApplication")), "sharedApplication");
        }

        MemorySegment mainMenu() throws Throwable {
            return send(nsApp(), "mainMenu");
        }
    }
}
