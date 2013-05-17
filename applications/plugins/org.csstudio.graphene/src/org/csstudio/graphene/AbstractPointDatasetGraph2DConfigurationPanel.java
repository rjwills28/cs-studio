/**
 * 
 */
package org.csstudio.graphene;

import org.csstudio.ui.util.AbstractConfigurationPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 * 
 */
public class AbstractPointDatasetGraph2DConfigurationPanel extends
		AbstractConfigurationPanel {
	
	private Text textDataFormula;
	private Text textXColumnFormula;
	private Text textYColumnFormula;
	private Button btnShowAxis;

	public AbstractPointDatasetGraph2DConfigurationPanel(Composite parent,
			int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblDataFormula = new Label(this, SWT.NONE);
		lblDataFormula.setText("Data Formula:");

		textDataFormula = new Text(this, SWT.BORDER);
		textDataFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textDataFormula.addListener(SWT.DefaultSelection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				changeSupport.firePropertyChange("dataFormula", null,
						textDataFormula.getText());

			}
		});

		Label lblXColumnFormula = new Label(this, SWT.NONE);
		lblXColumnFormula.setText("X Column Formula:");

		textXColumnFormula = new Text(this, SWT.BORDER);
		textXColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		textXColumnFormula.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				changeSupport.firePropertyChange("xColumnFormula", null,
						textXColumnFormula.getText());
			}
		});

		Label lblYColumnFormula = new Label(this, SWT.NONE);
		lblYColumnFormula.setText("Y Column Formula:");

		textYColumnFormula = new Text(this, SWT.BORDER);
		textYColumnFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		textYColumnFormula.addListener(SWT.DefaultSelection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				changeSupport.firePropertyChange("yColumnFormula", null,
						textYColumnFormula.getText());
			}
		});
		
		new Label(this, SWT.NONE);

		btnShowAxis = new Button(this, SWT.CHECK);
		btnShowAxis.setText("Show Axis Scroll");
		btnShowAxis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeSupport.firePropertyChange("showAxisScroll", null,
						getShowAxis());
			}
		});
	}

	public String getDataFormula() {
		return textDataFormula.getText();
	}

	public void setDataFormula(String dataFormula) {
		if (dataFormula != null) {
			this.textDataFormula.setText(dataFormula);
			changeSupport.firePropertyChange("dataFormula", null,
					textDataFormula.getText());
		}
	}

	public String getXColumnFormula() {
		return textXColumnFormula.getText();
	}

	public void setXColumnFormula(String xColumnFormula) {
		if (xColumnFormula != null) {
			this.textXColumnFormula.setText(xColumnFormula);
			changeSupport.firePropertyChange("xColumnFormula", null,
					textXColumnFormula.getText());
		}
	}

	public String getYColumnFormula() {
		return textYColumnFormula.getText();
	}

	public void setYColumnFormula(String yColumnFormula) {
		if (yColumnFormula != null) {
			this.textYColumnFormula.setText(yColumnFormula);
			changeSupport.firePropertyChange("yColumnFormula", null,
					textYColumnFormula.getText());
		}
	}

	public boolean getShowAxis() {
		return this.btnShowAxis.getSelection();
	}

	public void setShowAxis(boolean showAxis) {
		this.btnShowAxis.setSelection(showAxis);
		changeSupport.firePropertyChange("showAxisScroll", null, getShowAxis());
	}

}
