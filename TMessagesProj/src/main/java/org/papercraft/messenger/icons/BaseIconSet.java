/*

 This is the source code of Papercraft for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2023

*/

package org.papercraft.messenger.icons;

import android.util.SparseIntArray;

public abstract class BaseIconSet {
    public SparseIntArray iconPack = new SparseIntArray();

    public Integer getIcon(Integer id) {
        return iconPack.get(id, id);
    }
}
