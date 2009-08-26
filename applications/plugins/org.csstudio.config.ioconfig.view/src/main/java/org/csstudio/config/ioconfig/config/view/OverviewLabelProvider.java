package org.csstudio.config.ioconfig.config.view;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class OverviewLabelProvider implements ITableLabelProvider, IColorProvider {

    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof Channel) {
            Channel channel = (Channel) element;
            switch (columnIndex) {
                case 0:
                    return "  ";
                case 1:
                    return channel.getFullChannelNumber()+"";
                case 2:
                    return channel.getName();
                case 3:
                    return channel.getIoName();
                case 4:
                    return channel.getEpicsAddressStringNH();
                case 5:
                    return channel.getDescription();
                case 6:
                    return channel.getChannelType().name();
                default:
                    break;
            }
            
        }
        if (element instanceof Module) {
            Module module = (Module) element;
            switch (columnIndex) {
                case 0:
                    return module.getSortIndex()+"";
                case 2:
                    return module.getName();
                default:
                    break;
            }

        }
        return null;
    }

    public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub
        
    }

    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    public boolean isLabelProperty(Object element, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub
        
    }

    public Color getBackground(Object element) {
        if (element instanceof Module) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
        }
        return null;
    }

    public Color getForeground(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

}
