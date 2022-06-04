/*

  HashGarten 0.9.0 - a GUI to calculate and verify hashes, powered by Jacksum
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
package net.jacksum.gui.interfaces;

/**
 *
 * @author Johann N. Löfflmann
 */
public interface AlgorithmSelectionInterface {

    public String getSelection();

    public void setSelection(String algos);

    public int getDataSize();
}
