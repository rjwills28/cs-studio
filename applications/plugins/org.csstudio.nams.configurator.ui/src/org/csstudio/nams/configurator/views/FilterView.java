package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.composite.FilteredListVarianteA;
import org.csstudio.nams.configurator.controller.AbstractConfigurationChangeListener;
import org.csstudio.nams.configurator.controller.ConfigurationBeanController;
import org.csstudio.nams.configurator.modelmapping.ModelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FilterView extends ViewPart {

	private static ModelFactory modelFactory;
	public static final String ID = "org.csstudio.nams.configurator.filter";
	private static ConfigurationBeanController controller;
	public FilterView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new FilteredListVarianteA(parent, SWT.None, controller) {
			@Override
			protected Object[] getTableInput() {
				return modelFactory.getFilterBeans();
			}

			@Override
			protected void registerControllerListener() {
				controller.addConfigurationChangedListener(new AbstractConfigurationChangeListener(){
					@Override
					protected void updateFilter() {
						updateView();
					}
						});

				}
		};
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static void staticInject(ModelFactory modelFactory, ConfigurationBeanController controller) {
		FilterView.modelFactory = modelFactory;
		FilterView.controller = controller;
		
	}

}
