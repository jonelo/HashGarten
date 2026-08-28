/*

  HashGarten 0.20.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2024 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
package net.jacksum.gui.constants;

/**
 *
 * @author Johann
 */
public class PropertyKeys {
    public final static String JACKSUM_PARAMETERS_BASE64 = "jacksum.parameters.base64";
    public final static String GUI_THEME = "gui.theme";
    public final static String GUI_SMARTPOSITIONED = "gui.smartpositioned";
    public final static String GUI_ALWAYSONTOP = "gui.alwaysontop";
    public final static String GUI_STAYOPEN = "gui.stayopen";
    // Jacksum maps both key types "Text" and "Password" onto Sequence.Type.TXT,
    // so which one the user has picked has to be remembered by the GUI itself
    public final static String GUI_KEYTYPE = "gui.keytype";
}
