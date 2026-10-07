/*

  HashGarten 0.21.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import net.jacksum.gui.GUIHelper;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

/**
 * Provides the icon that the Finder shows for a file or a directory. On macOS,
 * FileSystemView.getSystemIcon() only returns the generic file and folder icons of the look and
 * feel, and the JDK's own access to the Finder icons is not exported. So the icon is taken from
 * NSWorkspace through the Foreign Function and Memory API, like in MacAppMenu.
 *
 * NSWorkspace and a newly created NSImage may be used on any thread, so the EDT is fine.
 *
 * None of the methods throws: after a failure, the class is disabled and null is returned, so
 * that the caller falls back to the icons of the look and feel.
 */
public final class MacFileIcons {

    // NSBitmapImageFileTypePNG
    private static final long PNG = 4;

    private static volatile boolean disabled = !SystemInfo.isMacOS;

    private MacFileIcons() {
    }

    /**
     * Returns the Finder icon of a file or a directory, sharp on HiDPI screens as well.
     *
     * @param file the file or directory, which must exist
     * @param size the logical size of the icon, e.g. 16
     * @return the icon, or null if it is not available (e.g. not on macOS)
     */
    public static Icon iconFor(File file, int size) {
        if (disabled) {
            return null;
        }
        MemorySegment pool = MemorySegment.NULL;
        try (Arena arena = Arena.ofConfined()) {
            ObjC objc = new ObjC(arena);
            // the EDT has no autorelease pool, without one every autoreleased object would leak
            pool = (MemorySegment) ObjC.POOL_PUSH.invokeExact();
            MemorySegment workspace = objc.send(objc.getClass("NSWorkspace"), "sharedWorkspace");
            MemorySegment path = objc.sendPointer(objc.getClass("NSString"), "stringWithUTF8String:",
                    arena.allocateFrom(file.getAbsolutePath()));
            MemorySegment image = objc.sendPointer(workspace, "iconForFile:", path);
            if (image.address() == 0) {
                return null;
            }
            List<BufferedImage> images = new ArrayList<>();
            for (int scale = 1; scale <= 2; scale++) {
                BufferedImage png = render(objc, arena, image, size * scale);
                if (png != null) {
                    images.add(png);
                }
            }
            if (images.isEmpty()) {
                return null;
            }
            images.sort(Comparator.comparingInt(BufferedImage::getWidth));
            // the base image determines the size of the icon, so it must have the logical size
            if (images.get(0).getWidth() != size) {
                images.add(0, scale(images.get(0), size));
            }
            return new ImageIcon(new BaseMultiResolutionImage(images.toArray(new Image[0])));
        } catch (Throwable t) {
            disabled = true;
            GUIHelper.debug(String.format("Could not get the Finder icon of %s, using generic icons from now on: %s", file, t));
            return null;
        } finally {
            if (pool.address() != 0) {
                try {
                    ObjC.POOL_POP.invokeExact(pool);
                } catch (Throwable t) {
                    disabled = true;
                    GUIHelper.debug(String.format("Could not release an autorelease pool: %s", t));
                }
            }
        }
    }

    // renders an NSImage as a PNG with the given number of pixels per side and decodes it
    private static BufferedImage render(ObjC objc, Arena arena, MemorySegment image, int pixels) throws Throwable {
        // NSRect {{0, 0}, {pixels, pixels}}
        MemorySegment rect = arena.allocate(JAVA_DOUBLE, 4);
        rect.setAtIndex(JAVA_DOUBLE, 2, pixels);
        rect.setAtIndex(JAVA_DOUBLE, 3, pixels);
        MemorySegment cgImage = objc.sendThreePointers(image, "CGImageForProposedRect:context:hints:",
                rect, MemorySegment.NULL, MemorySegment.NULL);
        if (cgImage.address() == 0) {
            return null;
        }
        MemorySegment rep = objc.send(objc.getClass("NSBitmapImageRep"), "alloc");
        rep = objc.sendPointer(rep, "initWithCGImage:", cgImage);
        if (rep.address() == 0) {
            return null;
        }
        objc.send(rep, "autorelease");
        MemorySegment properties = objc.send(objc.getClass("NSDictionary"), "dictionary");
        MemorySegment data = objc.sendLongAndPointer(rep, "representationUsingType:properties:", PNG, properties);
        if (data.address() == 0) {
            return null;
        }
        long length = objc.sendReturningLong(data, "length");
        MemorySegment bytes = objc.send(data, "bytes").reinterpret(length);
        return ImageIO.read(new ByteArrayInputStream(bytes.toArray(java.lang.foreign.ValueLayout.JAVA_BYTE)));
    }

    private static BufferedImage scale(BufferedImage image, int size) {
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, size, size, null);
        g.dispose();
        return scaled;
    }

    /**
     * The calls into the Objective-C runtime that are needed here. objc_msgSend has to be called
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
        // void *objc_autoreleasePoolPush(void)
        static final MethodHandle POOL_PUSH = LINKER.downcallHandle(
                LIBOBJC.find("objc_autoreleasePoolPush").orElseThrow(), FunctionDescriptor.of(ADDRESS));
        // void objc_autoreleasePoolPop(void *)
        static final MethodHandle POOL_POP = LINKER.downcallHandle(
                LIBOBJC.find("objc_autoreleasePoolPop").orElseThrow(), FunctionDescriptor.ofVoid(ADDRESS));
        // id (id, SEL)
        private static final MethodHandle SEND = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
        // id (id, SEL, void *)
        private static final MethodHandle SEND_POINTER = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, ADDRESS));
        // id (id, SEL, void *, void *, void *)
        private static final MethodHandle SEND_THREE_POINTERS = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS, ADDRESS));
        // id (id, SEL, NSUInteger, id)
        private static final MethodHandle SEND_LONG_AND_POINTER = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_LONG, ADDRESS));
        // NSUInteger (id, SEL)
        private static final MethodHandle SEND_RETURNING_LONG = LINKER.downcallHandle(MSG_SEND,
                FunctionDescriptor.of(JAVA_LONG, ADDRESS, ADDRESS));

        private final Arena arena;

        ObjC(Arena arena) {
            this.arena = arena;
        }

        MemorySegment getClass(String name) throws Throwable {
            return (MemorySegment) GET_CLASS.invokeExact(arena.allocateFrom(name));
        }

        MemorySegment selector(String name) throws Throwable {
            return (MemorySegment) SEL_REGISTER_NAME.invokeExact(arena.allocateFrom(name));
        }

        MemorySegment send(MemorySegment receiver, String selector) throws Throwable {
            return (MemorySegment) SEND.invokeExact(receiver, selector(selector));
        }

        MemorySegment sendPointer(MemorySegment receiver, String selector, MemorySegment argument) throws Throwable {
            return (MemorySegment) SEND_POINTER.invokeExact(receiver, selector(selector), argument);
        }

        MemorySegment sendThreePointers(MemorySegment receiver, String selector,
                MemorySegment a, MemorySegment b, MemorySegment c) throws Throwable {
            return (MemorySegment) SEND_THREE_POINTERS.invokeExact(receiver, selector(selector), a, b, c);
        }

        MemorySegment sendLongAndPointer(MemorySegment receiver, String selector, long a, MemorySegment b)
                throws Throwable {
            return (MemorySegment) SEND_LONG_AND_POINTER.invokeExact(receiver, selector(selector), a, b);
        }

        long sendReturningLong(MemorySegment receiver, String selector) throws Throwable {
            return (long) SEND_RETURNING_LONG.invokeExact(receiver, selector(selector));
        }
    }
}
