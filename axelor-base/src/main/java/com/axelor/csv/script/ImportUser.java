/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.csv.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import com.axelor.auth.db.User;
import com.axelor.meta.MetaFiles;
import com.google.common.base.Strings;
import com.google.inject.Inject;

public class ImportUser {
	
	
	@Inject
	MetaFiles metaFiles;
	
	
	// Returns the contents of the file in a byte array.
    public Object importUser(Object bean, Map<String,Object> values) throws IOException {        
    	assert bean instanceof User;
		
		User user = (User) bean;
		final Path path = (Path) values.get("__path__");
	    String fileName = (String) values.get("picture_fileName");
	    if(Strings.isNullOrEmpty((fileName)))  {  return bean;  }
		
	    final File image = path.resolve(fileName).toFile(); 
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)image.length()];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(image);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            user.setImage(bytes);
        } finally {
            is.close();
        }
        return bean;
    }
}
