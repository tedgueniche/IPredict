/* HEADER 
If you use this code don’t forget to reference us :) BibTeX: http://www.cs.technion.ac.il/~rani/el-yaniv_bib.html#BegleiterEY04 

This code is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License 
as published by the Free Software Foundation; either version 2 
of the License, or (at your option) any later version. 

This code is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License (<a href="http://www.gnu.org/copyleft/gpl.html">GPL</a>) for more details.*/ 
 
/*
 * Created on Jul 9, 2003
 */

package vmm.algs.lzms;

import java.util.HashMap;


/**
 * @author Moti
 *
 * LZNodeHashStorage uses a hash to store/retrieve LZNodes
 */
public class LZNodeHashStorage
    implements LZNodeStorage {
  HashMap<Integer, Object> storage;

  public void put(int key, LZNode node) {
    if (storage == null) {
      storage = new HashMap<Integer, Object>(2);
    }
    storage.put(key, node);
  }

  public LZNode get(int key) {
    if (storage == null) {
      return null;
    }
    return (LZNode) storage.get(key);
  }

  public LZNode[] toArray() {
    /**@ron handle null storage(!)*/
    Object[] objects =
        (storage == null) ? new Object[0] : storage.values().toArray(); 
    LZNode[] result = new LZNode[objects.length];

    System.arraycopy(objects, 0, result, 0, objects.length);

    return result;
  }
}
