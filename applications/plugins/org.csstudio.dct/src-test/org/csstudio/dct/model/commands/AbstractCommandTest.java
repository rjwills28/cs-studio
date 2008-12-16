package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.metamodel.persistence.internal.MetaModelService;
import org.csstudio.dct.model.internal.Project;
import org.junit.Before;

public abstract class AbstractCommandTest {
	protected Project project;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		project = new Project("test", UUID.randomUUID());
		MetaModelService.getInstance().read(getClass().getResource("test.dbd").getFile());
		
		doSetUp();
	}

	protected abstract void doSetUp() throws Exception;
}
