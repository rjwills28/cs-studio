package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.LdaptreePlugin;
import org.csstudio.alarm.treeView.views.models.ContextTreeObject;
import org.csstudio.alarm.treeView.views.models.ContextTreeParent;
import org.csstudio.alarm.treeView.views.models.ISimpleTreeObject;
import org.csstudio.alarm.treeView.views.models.ISimpleTreeParent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;


/**
 * Provides the content for the alarm tree view.
 */
public class AlarmTreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	/**
	 * Creates a new alarm tree content provider.
	 */
	public AlarmTreeContentProvider() {
		super();
	}
	
	/**
	 * Returns the children of the object passed to this method.
	 * @param parent the input element.
	 * @return the children of the input element.
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
	
	/**
	 * Returns the parent of the object passed to this method.
	 * @param child the child element.
	 * @return the child element's parent, or {@code null} if it has none or if
	 * the parent element cannot be computed.
	 */
	public Object getParent(Object child) {
		if (child instanceof ISimpleTreeObject) {
			return ((ISimpleTreeObject)child).getParent();
		}
		return null;
	}
	
	/**
	 * Returns the children of the object passed to this method.
	 * @param parent the input element.
	 * @return the children of the input element.
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof IViewSite){
			return LdaptreePlugin.getDefault().getConnections().toArray();}
		else if (parent instanceof ISimpleTreeParent) {
			return ((ISimpleTreeParent)parent).getChildren();
		}
		else if (parent instanceof ISimpleTreeObject) {
			return new Object[0];
		} else {
			throw new IllegalArgumentException("Illegal object " + parent + " in tree");
		}
	}
	
	/**
	 * Returns whether the given element has children.
	 * @param parent the element
	 * @return {@code true} if the given element has children, {@code false}
	 * otherwise.
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof ISimpleTreeParent)
			return ((ISimpleTreeParent)parent).hasChildren();
		return false;
	}
	
	/**
	 * Disposes of this content provider.
	 */
	public void dispose() {
		// nothing to do
	}

	/**
	 * Notifies this content provider that the viewer's input has switched.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// content provider current does not work based on input objects
	}

}