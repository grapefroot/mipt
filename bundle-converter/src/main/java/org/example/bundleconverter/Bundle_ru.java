package org.example.bundleconverter;

import java.util.ListResourceBundle;

import java.util.ResourceBundle;

/**

 * Resource bundle with general strings (concerning files).

 * This implementation is used if the current user locale is Russian (ru_RU).

 * Note that the encoding of this source file is Cp1251 (windows-1251)

 *  so this bundle is compiled correctly only on computers with russian locale.

 *  Such an approach is not recommended: use UTF-8 encoding for all sources! 

 */

public class Bundle_ru extends ListResourceBundle {

/**

 * @see java.util.ListResourceBundle

 */

protected final Object[][] getContents() { 

return new Object[][]{

{"Error", "Ошибка"}, 

{"File", "Файл"}, 

{"inFile", "в файле"}, 

{"notFound", "не найден"}, 

{"CantDelete", "Невозможно удалить"}, 

{"CantCreate", "Невозможно создать"}//not used yet 

};

}

}
