package org.example.bundleconverter;

import java.util.ListResourceBundle;

import java.util.ResourceBundle;

/**

 * Resource bundle with general strings (concerning files).

 * This implementation is used if a bundle for the current user locale is not found.

 */

public class Bundle extends ListResourceBundle {

/**

 * @see java.util.ListResourceBundle

 */

protected final Object[][] getContents() { 

return new Object[][]{

{"Error", "Error"},

{"File", "File"}, 

{"inFile", "in file"}, 

{"notFound", "not found"}, 

{"CantDelete", "Can't delete"}, 

{"CantCreate", "Can't create"}//not used yet 

};

}

}
