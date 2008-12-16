package org.csstudio.dct.metamodel.persistence.internal;

import java.io.File;
import java.util.Enumeration;
import java.util.Set;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.internal.Choice;
import org.csstudio.dct.metamodel.internal.DatabaseDefinition;
import org.csstudio.dct.metamodel.internal.FieldDefinition;
import org.csstudio.dct.metamodel.internal.MenuDefinition;
import org.csstudio.dct.metamodel.internal.RecordDefinition;
import org.csstudio.dct.metamodel.persistence.IMetaModelService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.cosylab.vdct.dbd.DBDData;
import com.cosylab.vdct.dbd.DBDFieldData;
import com.cosylab.vdct.dbd.DBDMenuData;
import com.cosylab.vdct.dbd.DBDRecordData;
import com.cosylab.vdct.dbd.DBDResolver;

public class MetaModelService implements IMetaModelService {
	private static MetaModelService instance;

	private MetaModelService() {

	}

	public static final MetaModelService getInstance() {
		if (instance == null) {
			instance = new MetaModelService();
		}

		return instance;

	}

	public IDatabaseDefinition read(String path) {
		IDatabaseDefinition result = null;

		if (path != null && path.length() > 0) {

			// .. file system search
			File file = new File(path);

			if (file.exists()) {
				result = doRead(file.getAbsolutePath());
			}

			// .. workspace search
			if (result == null) {
				IFile workspaceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));

				if (workspaceFile != null) {
					result = doRead(workspaceFile.getLocation().toOSString());
				}
			}
		}

		return result;
	}

	private IDatabaseDefinition doRead(String path) {
		DatabaseDefinition databaseDefinition = new DatabaseDefinition("1.0");

		// .. use the VDCT parser
		DBDResolver resolver = new DBDResolver();
		DBDData data = new DBDData();
		resolver.resolveDBD(data, path);

		// .. transform the VDCT dbd model to our CSS-DCTs meta model

		// .. menus

		// .. records
		Enumeration<String> it = data.getRecordNames();
		while (it.hasMoreElements()) {
			String n = it.nextElement();
			DBDRecordData recordData = data.getDBDRecordData(n);

			RecordDefinition recordDefinition = new RecordDefinition(n);
			databaseDefinition.addRecordDefinition(recordDefinition);

			for (String fieldName : (Set<String>) recordData.getFields().keySet()) {
				DBDFieldData fieldData = recordData.getDBDFieldData(fieldName);

				FieldDefinition fieldDefinition = new FieldDefinition(fieldName, DBDResolver.getFieldType(fieldData.getField_type()));

				// .. prompt
				fieldDefinition.setPrompt(fieldData.getPrompt_value());

				// .. menu
				String menuName = fieldData.getMenu_name();

				if (menuName != null && menuName.length() > 0) {
					DBDMenuData menuData = data.getDBDMenuData(menuName);

					MenuDefinition menuDefinition = new MenuDefinition(menuName);

					for (String choiceId : (Set<String>) menuData.getChoices().keySet()) {
						String description = menuData.getChoices().get(choiceId).toString();
						menuDefinition.addChoice(new Choice(choiceId, description));
					}

					System.err.println(fieldDefinition.getName());
					fieldDefinition.setMenuDefinition(menuDefinition);
				}

				recordDefinition.addFieldDefinition(fieldDefinition);
			}
		}

		return databaseDefinition;
	}

}