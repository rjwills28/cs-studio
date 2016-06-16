/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmInt;
import org.csstudio.opibuilder.converter.model.EdmString;
import org.csstudio.opibuilder.converter.model.Edm_xyGraphClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 *
 * @author Lei Hu, Xihui Chen
 */
public class Opi_xyGraphClass extends OpiWidget {

    private static Logger log = Logger
            .getLogger("org.csstudio.opibuilder.converter.writer.Opi_xyGraphClass");
    private static final String typeId = "dawn.xygraph";
    private static final String name = "EDM xyGraph";
    private static final String version = "1.0";

    // These are copied from the dawnxygraph:
    //  org.eclipse.nebula.visualization.xygraph.figures.Trace
    // Duplicated rather than imported to reduce the dependencies of the converter
    private static final int SOLID_LINE = 0;
    private static final int DASH_LINE = 1;
    private static final int POINT = 2;
    private static final int BAR = 3;

    private static final int STYLE_NONE = 0;
    private static final int STYLE_POINT = 1;
    private static final int STYLE_CIRCLE = 2;
    private static final int STYLE_SQUARE = 6;
    private static final int STYLE_DIAMOND = 8;

    private static final String TRACE_I_UPDATE_DELAY = "trace_%d_update_delay";
    private static final String TRACE_I_CONCATENATE_DATA = "trace_%d_concatenate_data";

    private static final String TRACE_N_X_PV = "trace_%s_x_pv";
    private static final String TRACE_N_Y_PV = "trace_%s_y_pv";
    private static final String TRACE_N_TRACE_COLOR = "trace_%s_trace_color";
    private static final String TRACE_N_TRACE_TYPE = "trace_%s_trace_type";
    private static final String TRACE_N_POINT_STYLE = "trace_%s_point_style";
    private static final String TRACE_N_POINT_SIZE = "trace_%s_point_size";
    private static final String TRACE_N_BUFFER_SIZE = "trace_%s_buffer_size";
    private static final String TRACE_N_LINE_WIDTH = "trace_%s_line_width";
    private static final String TRACE_N_Y_AXIS_INDEX = "trace_%s_y_axis_index";
    private static final String TRACE_N_UPDATE_MODE = "trace_%s_update_mode";
    private static final String TRACE_N_ANTI_ALIAS = "trace_%s_anti_alias";

    // format takes index ('0', '1', ...) and axis ('x' or 'y') arguments
    private static final String TRACE_TOOLTIP_UNIT = "$(trace_%1$s_%2$s_pv)\n$(trace_%1$s_%2$s_pv_value)";

    /**
     * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
     */
    public Opi_xyGraphClass(Context con, Edm_xyGraphClass r) {
        super(con, r);
        setTypeId(typeId);
        setVersion(version);
        setName(name);

        new OpiBoolean(widgetContext, "show_toolbar", false);

        new OpiInt(widgetContext, "axis_count", 3); // axis count
        // Title
        if (r.getGraphTitle() != null) {
            new OpiString(widgetContext, "title", r.getGraphTitle());
        }

        setAxisLabelProperty("axis_0_axis_title", r.getXLabel());
        setAxisLabelProperty("axis_1_axis_title", r.getYLabel());
        setAxisLabelProperty("axis_2_axis_title", r.getY2Label());

        new OpiBoolean(widgetContext, "axis_2_left_bottom_side", false);

        new OpiColor(widgetContext, "axis_0_axis_color", r.getFgColor(), r);
        new OpiColor(widgetContext, "axis_1_axis_color", r.getFgColor(), r);
        new OpiColor(widgetContext, "axis_2_axis_color", r.getFgColor(), r);

        new OpiColor(widgetContext, "background_color", con.getRootDisplay().getBgColor(), r);

        new OpiColor(widgetContext, "plot_area_background_color", r.getBgColor(), r);

        new OpiColor(widgetContext, "axis_0_grid_color", r.getGridColor(), r);
        new OpiColor(widgetContext, "axis_1_grid_color", r.getGridColor(), r);
        new OpiColor(widgetContext, "axis_2_grid_color", r.getGridColor(), r);

        if (r.isBorder()) {
            // EDM xygraph borders always one pixel, black.
            new OpiInt(widgetContext, "border_width", 1);
            new OpiInt(widgetContext, "border_style", 1);
            new OpiColor(widgetContext, "border_color", new EdmColor("Black", 0, 0 ,0), r);
        } else {
            new OpiInt(widgetContext, "border_width", 0);
            new OpiInt(widgetContext, "border_style", 0);
        }

        new OpiBoolean(widgetContext, "show_plot_area_border", r.isPlotAreaBorder());

        new OpiBoolean(widgetContext, "axis_0_visible", r.isShowXAxis());
        new OpiBoolean(widgetContext, "axis_1_visible", r.isShowYAxis());
        new OpiBoolean(widgetContext, "axis_2_visible", r.isShowY2Axis());

        new OpiDouble(widgetContext, "axis_0_minimum", r.getxMin());
        new OpiDouble(widgetContext, "axis_1_minimum", r.getyMin());
        new OpiDouble(widgetContext, "axis_2_minimum", r.getY2Min());

        new OpiDouble(widgetContext, "axis_0_maximum", r.getxMax());
        new OpiDouble(widgetContext, "axis_1_maximum", r.getyMax());
        new OpiDouble(widgetContext, "axis_2_maximum", r.getY2Max());

        // CSS only has coarse grid lines, show them if any EDM Grid is enabled
        new OpiBoolean(widgetContext, "axis_0_show_grid",
                r.isxShowMajorGrid() || r.isxShowMinorGrid() || r.isxShowLabelGrid());
        new OpiBoolean(widgetContext, "axis_1_show_grid",
                r.isyShowMajorGrid() || r.isyShowMinorGrid() || r.isyShowLabelGrid());
        new OpiBoolean(widgetContext, "axis_2_show_grid",
                r.isY2ShowMajorGrid() || r.isY2ShowMinorGrid() || r.isY2ShowLabelGrid());

        // There is no legend on EDM xygraphs.
        new OpiBoolean(widgetContext, "show_legend", false);

        // All fonts on EDM graphs are the same
        if (r.getFont().isExistInEDL()) {
            if (r.getGraphTitle() != null) {
                new OpiFont(widgetContext, "title_font", r.getFont());
            }
            if (r.isShowXAxis()) {
                new OpiFont(widgetContext, "axis_0_scale_font", r.getFont());
                new OpiFont(widgetContext, "axis_0_title_font", r.getFont());
            }
            if (r.isShowYAxis()) {
                new OpiFont(widgetContext, "axis_1_scale_font", r.getFont());
                new OpiFont(widgetContext, "axis_1_title_font", r.getFont());
            }
            if (r.isShowY2Axis()) {
                new OpiFont(widgetContext, "axis_2_scale_font", r.getFont());
                new OpiFont(widgetContext, "axis_2_title_font", r.getFont());
            }
        }

        new OpiInt(widgetContext, "axis_0_time_format",
                (r.getXAxisStyle() != null && (r.getXAxisStyle().equals("time"))) ? 7 : 0);

        new OpiBoolean(widgetContext, "axis_1_logScale", r.getYAxisStyle() != null
                && (r.getYAxisStyle().equals("log10")));

        new OpiBoolean(widgetContext, "axis_2_logScale", r.getY2AxisStyle() != null
                && (r.getY2AxisStyle().equals("log10")));

        new OpiInt(widgetContext, "axis_1_time_format", 0);
        new OpiInt(widgetContext, "axis_2_time_format", 0);

        if (r.getTriggerPv() != null)
            new OpiString(widgetContext, "trigger_pv", r.getTriggerPv());


        // Axis properties. EDM doesn't instantly scale in even with no threshold
        for (int i = 0; i < 2; i++) {
            new OpiBoolean(widgetContext, "axis_" + i + "_auto_scale",
                    r.isAutoScaleBothDirections());
            new OpiDouble(widgetContext, "axis_" + i + "_auto_scale_threshold",
                    Math.max(r.getAutoScaleThreshPct() / 100, 0.95));
        }

        // Don't assume defaults are true or false
        if(r.getxAxisSrc()!=null && r.getxAxisSrc().equals("AutoScale")) {
            new OpiBoolean(widgetContext, "axis_0_auto_scale", true);
        } else {
            new OpiBoolean(widgetContext, "axis_0_auto_scale", false);
        }

        if(r.getyAxisSrc()!=null && r.getyAxisSrc().equals("AutoScale")) {
            new OpiBoolean(widgetContext, "axis_1_auto_scale", true);
        } else {
            new OpiBoolean(widgetContext, "axis_1_auto_scale", false);
        }

        if(r.getY2AxisSrc()!=null && r.getY2AxisSrc().equals("AutoScale")) {
            new OpiBoolean(widgetContext, "axis_2_auto_scale", true);
        } else {
            new OpiBoolean(widgetContext, "axis_2_auto_scale", false);
        }

        if(r.getxAxisStyle()!=null && r.getxAxisStyle().equals("log10")) {
            new OpiBoolean(widgetContext, "axis_0_log_scale", true);
        } else {
            new OpiBoolean(widgetContext, "axis_0_log_scale", false);
        }

        if(r.getyAxisStyle()!=null && r.getyAxisStyle().equals("log10")) {
            new OpiBoolean(widgetContext, "axis_1_log_scale", true);
        } else {
            new OpiBoolean(widgetContext, "axis_1_log_scale", false);
        }

        // trace properties
        new OpiInt(widgetContext, "trace_count", r.getNumTraces());

        for(int i = 0; i < r.getNumTraces(); i++){
            new OpiInt(widgetContext, String.format(TRACE_I_UPDATE_DELAY, i), r.getUpdateTimerMs());
            //give it a big buffer if it is waveform, edm will show all waveform values regardless nPts.
            if (r.getPlotMode() == null && r.getnPts() < 5){ //assume it is a waveform
                new OpiBoolean(widgetContext, String.format(TRACE_I_CONCATENATE_DATA, i), false);
                new OpiInt(widgetContext, String.format(TRACE_N_BUFFER_SIZE, String.valueOf(i)), 65536);
            } else {
                new OpiInt(widgetContext, String.format(TRACE_N_BUFFER_SIZE, String.valueOf(i)), r.getnPts());
            }
            // force anti-aliasing 'off' to remove 'blur'
            new OpiBoolean(widgetContext, String.format(TRACE_N_ANTI_ALIAS,  String.valueOf(i)), false);
        }

        // PV X,Y
        List<String> tooltips = new ArrayList<>();
        if (r.getYPv().isExistInEDL()) {
            for (Entry<String, EdmString> entry : r.getYPv().getEdmAttributesMap().entrySet()) {
                new OpiString(widgetContext, String.format(TRACE_N_Y_PV, entry.getKey()), convertPVName(entry.getValue().get()));
                tooltips.add(String.format(TRACE_TOOLTIP_UNIT, entry.getKey(), 'y'));
            }
        }

        if (r.getXPv().isExistInEDL()) {
            for (Entry<String, EdmString>  entry: r.getXPv().getEdmAttributesMap().entrySet()) {
                new OpiString(widgetContext, String.format(TRACE_N_X_PV, entry.getKey()), convertPVName(entry.getValue().get()));
                tooltips.add(String.format(TRACE_TOOLTIP_UNIT, entry.getKey(), 'x'));
            }
        }

        // construct tooltip string from all defined x- and y-PVs
        new OpiString(widgetContext, "tooltip", tooltips.stream().collect(Collectors.joining("\n")));

        // Set Graph PV to y0 to enable tooltips and middle-click
        if (r.getYPv().isExistInEDL()) {
            new OpiString(widgetContext, "pv_name", String.format("$(%s)", String.format(TRACE_N_Y_PV, '0')));
        }

        if (r.getPlotColor().isExistInEDL()) {
            for (Entry<String, EdmColor> entry : r.getPlotColor().getEdmAttributesMap().entrySet()) {
                new OpiColor(widgetContext, String.format(TRACE_N_TRACE_COLOR, entry.getKey()), entry.getValue(), r);
            }
        }

        if(r.getPlotStyle().isExistInEDL()){
            for (Entry<String, EdmString> entry : r.getPlotStyle().getEdmAttributesMap().entrySet()) {
                final String value = entry.getValue().get();
                final String key = entry.getKey();

                if(value.equals("needle")){
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, key), BAR);
                } else if (value.equals("point")) {
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, key), POINT);
                    new OpiInt(widgetContext, String.format(TRACE_N_POINT_STYLE, key), STYLE_POINT);
                    // EDM point graphs are always one pixel
                    new OpiInt(widgetContext, String.format(TRACE_N_POINT_SIZE, key), 1);
                } else if (value.equals("single point")) {
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, key), POINT);
                    new OpiInt(widgetContext, String.format(TRACE_N_BUFFER_SIZE, key), 1);
                } else {
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, key), SOLID_LINE);
                }
            }
        }

        if(r.getLineThickness().isExistInEDL()){
            for (Entry<String, EdmInt> entry : r.getLineThickness().getEdmAttributesMap().entrySet())
                new OpiInt(widgetContext,
                        String.format(TRACE_N_LINE_WIDTH, entry.getKey()), entry.getValue().get());
        }

        if (r.getLineStyle().isExistInEDL()) {
            for (Entry<String, EdmString> entry : r.getLineStyle().getEdmAttributesMap().entrySet()) {
                if (entry.getValue().get().equals("dash")) {
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, entry.getKey()), DASH_LINE);
                }
            }
        }

        if(r.getPlotSymbolType().isExistInEDL()){
            for (Entry<String, EdmString> entry : r.getPlotSymbolType().getEdmAttributesMap().entrySet()) {

                int style = STYLE_NONE;
                if (entry.getValue().get().equals("circle")) {
                    style = STYLE_CIRCLE;
                }else if (entry.getValue().get().equals("square")) {
                    style = STYLE_SQUARE;
                }else if (entry.getValue().get().equals("diamond")) {
                    style = STYLE_DIAMOND;
                }
                new OpiInt(widgetContext, String.format(TRACE_N_POINT_STYLE, entry.getKey()), style);
            }
        }

        if(r.getOpMode().isExistInEDL()){
            for (Entry<String, EdmString> entry : r.getOpMode().getEdmAttributesMap().entrySet()) {
                //EDM will sort the data in this mode where BOY cannot, so plot it as points
                if (entry.getValue().get().equals("plot")) {
                    final String key = entry.getKey();
                    new OpiInt(widgetContext, String.format(TRACE_N_TRACE_TYPE, key), POINT);

                    if (!r.getPlotSymbolType().getEdmAttributesMap().containsKey(key)) {
                        new OpiInt(widgetContext, String.format(TRACE_N_POINT_STYLE, key), STYLE_POINT);
                    }
                    new OpiInt(widgetContext, String.format(TRACE_N_POINT_SIZE, key), 2);
                }
            }
        }

        if(r.getPlotUpdateMode().isExistInEDL()){
            for (Entry<String, EdmString> entry : r.getPlotUpdateMode().getEdmAttributesMap().entrySet()) {
                final String value = entry.getValue().get();

                int mode = 1;
                if (value.equals("xOrY")) {
                    mode = 0;
                }else if (value.equals("x")) {
                    mode = 2;
                }else if (value.equals("y")) {
                    mode = 3;
                }else if (value.equals("trigger")) {
                    mode = 4;
                }

                new OpiInt(widgetContext, String.format(TRACE_N_UPDATE_MODE, entry.getKey()), mode);
            }
        }

        if(r.getUseY2Axis().isExistInEDL()){
            for (Entry<String, EdmBoolean> entry : r.getUseY2Axis().getEdmAttributesMap().entrySet()) {
                if(entry.getValue().is())
                    new OpiInt(widgetContext, String.format(TRACE_N_Y_AXIS_INDEX, entry.getKey()), 2);
            }
        }

        log.config("Edm_xyGraphClass written.");
    }

    /**
     * Set the string property of an axis label.
     *
     * There's no way to turn off axis labels, so if we don't want one (i.e.
     * where the EDM label is null, set it to ""
     *
     * @param property Name of property to set
     * @param axisLabel Axis label to use (may be NULL)
     */
    private void setAxisLabelProperty(String property, String axisLabel) {
        if (axisLabel != null) {
            new OpiString(widgetContext, property, axisLabel);
        } else {
            new OpiString(widgetContext, property, "");
        }
    }

}
