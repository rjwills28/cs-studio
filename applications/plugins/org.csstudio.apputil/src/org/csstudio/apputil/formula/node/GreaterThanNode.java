package org.csstudio.apputil.formula.node;

import org.csstudio.apputil.formula.Node;

/** One computational node.
 *  @author Kay Kasemir
 */
public class GreaterThanNode extends AbstractBinaryNode
{
    public GreaterThanNode(final Node left, final Node right)
    {
        super(left, right);
    }
    
    public double eval()
    {
        final double a = left.eval();
        final double b = right.eval();
        return (a > b) ? 1.0 : 0.0;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "(" + left + " > " + right + ")";
    }
}
