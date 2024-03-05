package org.jetbrains.research.anticopypasterpython.config.advanced;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static org.jetbrains.research.anticopypasterpython.config.ProjectSettingsComponent.createLinkListener;
import java.util.EnumMap;

public class AdvancedProjectSettingsComponent {
    private JCheckBox totalKeywordCountInCheckBox;
    private JCheckBox requiredSubmetricCheckBox;
    private JCheckBox keywordDensityPerLineCheckBox;
    private JCheckBox requiredSubmetricCheckBox1;
    private JCheckBox continueCheckBox;
    private JCheckBox forCheckBox;
    private JCheckBox newCheckBox;
    private JCheckBox switchCheckBox;
    private JCheckBox assertCheckBox;
    private JCheckBox ifCheckBox;
    private JCheckBox throwCheckBox;
    private JCheckBox instanceofCheckBox;
    private JCheckBox intCheckBox;
    private JCheckBox finalCheckBox;
    private JCheckBox floatCheckBox;
    private JCheckBox synchronizedCheckBox;
    private JCheckBox thisCheckBox;
    private JCheckBox byteCheckBox;
    private JCheckBox returnCheckBox;
    private JCheckBox shortCheckBox;
    private JCheckBox finallyCheckBox;
    private JCheckBox superCheckBox;
    private JCheckBox whileCheckBox;
    private JCheckBox longCheckBox;
    private JCheckBox strictfpCheckBox;
    private JCheckBox tryCheckBox;
    private JCheckBox charCheckBox;
    private JCheckBox catchCheckBox;
    private JCheckBox transientCheckBox;
    private JCheckBox caseCheckBox;
    private JCheckBox elseCheckBox;
    private JCheckBox doubleCheckBox;
    private JCheckBox breakCheckBox;
    private JCheckBox doCheckBox;
    private JCheckBox booleanCheckBox;
    private JCheckBox totalConnectivityInSegmentCheckBox;
    private JCheckBox requiredSubmetricCheckBox2;
    private JCheckBox connectivityDensityPerLineCheckBox;
    private JCheckBox requiredSubmetricCheckBox3;
    private JCheckBox useTotalConnectivityCheckBox;
    private JCheckBox requiredSubmetricCheckBox4;
    private JCheckBox useFieldConnectivityCheckBox;
    private JCheckBox useMethodConnectivityCheckBox;
    private JCheckBox requiredSubmetricCheckBox5;
    private JCheckBox requiredSubmetricCheckBox6;
    private JCheckBox totalComplexityOfSegmentCheckBox;
    private JCheckBox requiredSubmetricCheckBox7;
    private JCheckBox complexityDensityPerLineCheckBox;
    private JCheckBox requiredSubmetricCheckBox8;
    private JCheckBox numberOfLinesInCheckBox;
    private JCheckBox requiredSubmetricCheckBox9;
    private JCheckBox numberOfSymbolsInCheckBox;
    private JCheckBox requiredSubmetricCheckBox10;
    private JCheckBox methodDeclarationAreaCheckBox;
    private JCheckBox methodDeclarationDepthDensityCheckBox;
    private JCheckBox requiredSubmetricCheckBox11;
    private JCheckBox requiredSubmetricCheckBox12;
    private JCheckBox densityOfSymbolsInCheckBox;
    private JCheckBox requiredSubmetricCheckBox13;
    private JCheckBox measureSizeOfSegmentCheckBox;
    private JCheckBox requiredSubmetricCheckBox14;
    private JCheckBox measureSizeOfMethodCheckBox;
    private JCheckBox requiredSubmetricCheckBox15;
    private JScrollPane mainScroll;
    private JPanel masterPanel;
    private JPanel helpLinkJPanel;
    private JLabel helpLabel;
    private JButton selectAllButton;
    private JButton selectNoneButton;
    private JLabel enabledKeywordsHelp;

    public JPanel getPanel() {
        return masterPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return mainScroll;
    }

    private void addConditionallyEnabledCheckboxGroup(JCheckBox ind, JCheckBox dep) {
        ind.addActionListener(e -> {
                    if (ind.isSelected()) {
                        dep.setEnabled(true);
                        dep.setSelected(true);
                    } else {
                        dep.setSelected(false);
                        dep.setEnabled(false);
                    }
                }
        );
    }

    public AdvancedProjectSettingsComponent() {

        createUIComponents();

        addConditionallyEnabledCheckboxGroup(totalKeywordCountInCheckBox, requiredSubmetricCheckBox);
        addConditionallyEnabledCheckboxGroup(keywordDensityPerLineCheckBox, requiredSubmetricCheckBox1);

        addConditionallyEnabledCheckboxGroup(totalConnectivityInSegmentCheckBox, requiredSubmetricCheckBox2);
        addConditionallyEnabledCheckboxGroup(connectivityDensityPerLineCheckBox, requiredSubmetricCheckBox3);

        addConditionallyEnabledCheckboxGroup(useTotalConnectivityCheckBox, requiredSubmetricCheckBox4);
        addConditionallyEnabledCheckboxGroup(useFieldConnectivityCheckBox, requiredSubmetricCheckBox5);
        addConditionallyEnabledCheckboxGroup(useMethodConnectivityCheckBox, requiredSubmetricCheckBox6);

        addConditionallyEnabledCheckboxGroup(totalComplexityOfSegmentCheckBox, requiredSubmetricCheckBox7);
        addConditionallyEnabledCheckboxGroup(complexityDensityPerLineCheckBox, requiredSubmetricCheckBox8);
        addConditionallyEnabledCheckboxGroup(methodDeclarationAreaCheckBox, requiredSubmetricCheckBox11);
        addConditionallyEnabledCheckboxGroup(methodDeclarationDepthDensityCheckBox, requiredSubmetricCheckBox12);

        addConditionallyEnabledCheckboxGroup(numberOfLinesInCheckBox, requiredSubmetricCheckBox9);
        addConditionallyEnabledCheckboxGroup(numberOfSymbolsInCheckBox, requiredSubmetricCheckBox10);
        addConditionallyEnabledCheckboxGroup(densityOfSymbolsInCheckBox, requiredSubmetricCheckBox13);
        addConditionallyEnabledCheckboxGroup(measureSizeOfSegmentCheckBox, requiredSubmetricCheckBox14);
        addConditionallyEnabledCheckboxGroup(measureSizeOfMethodCheckBox, requiredSubmetricCheckBox15);

    }

    private void createUIComponents() {
        // Hide the scrolling pane's border by making it the color of the background
        mainScroll = new JBScrollPane();
        mainScroll.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.background()));

        // Initialize "Select All" and "Select None" buttons
        selectAllButton = new JButton();
        selectNoneButton = new JButton();
        selectAllButton.setIcon(AllIcons.Actions.Selectall);
        selectNoneButton.setIcon(AllIcons.Actions.Unselectall);
        selectAllButton.addActionListener(e -> {
            EnumMap<PythonKeywords, Boolean> allTrueMap = new EnumMap<>(PythonKeywords.class);
            for (PythonKeywords keyword : PythonKeywords.values()) { allTrueMap.put(keyword, true); }
            setActiveKeywords(allTrueMap);
        });
        selectNoneButton.addActionListener(e -> {
            EnumMap<PythonKeywords, Boolean> allFalseMap = new EnumMap<>(PythonKeywords.class);
            for (PythonKeywords keyword : PythonKeywords.values()) { allFalseMap.put(keyword, false); }
            setActiveKeywords(allFalseMap);
        });

        // Initialize main help interface
        helpLabel = new JLabel();
        createLinkListener(helpLabel, "https://se4airesearch.github.io/AntiCopyPaster_Python_Website_Fall2023/pages/metric.html");
        helpLabel.setIcon(AllIcons.Ide.External_link_arrow);

        enabledKeywordsHelp = new JLabel();
        enabledKeywordsHelp.setIcon(AllIcons.General.ContextHelp);
    }

    public enum PythonKeywords {
        CONTINUE, FOR, NEW, SWITCH, ASSERT, SYNCHRONIZED, BOOLEAN, DO, IF, THIS, BREAK, DOUBLE, THROW, BYTE, ELSE,
        CASE, INSTANCEOF, RETURN, TRANSIENT, CATCH, INT, SHORT, TRY, CHAR, FINAL, FINALLY, LONG, STRICTFP, FLOAT, SUPER, WHILE
    }

    public void setActiveKeywords(EnumMap<PythonKeywords, Boolean> active) {
        continueCheckBox.setSelected(active.get(PythonKeywords.CONTINUE));
        forCheckBox.setSelected(active.get(PythonKeywords.FOR));
        newCheckBox.setSelected(active.get(PythonKeywords.NEW));
        switchCheckBox.setSelected(active.get(PythonKeywords.SWITCH));
        assertCheckBox.setSelected(active.get(PythonKeywords.ASSERT));
        synchronizedCheckBox.setSelected(active.get(PythonKeywords.SYNCHRONIZED));
        booleanCheckBox.setSelected(active.get(PythonKeywords.BOOLEAN));
        doCheckBox.setSelected(active.get(PythonKeywords.DO));
        ifCheckBox.setSelected(active.get(PythonKeywords.IF));
        thisCheckBox.setSelected(active.get(PythonKeywords.THIS));
        breakCheckBox.setSelected(active.get(PythonKeywords.BREAK));
        doubleCheckBox.setSelected(active.get(PythonKeywords.DOUBLE));
        throwCheckBox.setSelected(active.get(PythonKeywords.THROW));
        byteCheckBox.setSelected(active.get(PythonKeywords.BYTE));
        elseCheckBox.setSelected(active.get(PythonKeywords.ELSE));
        caseCheckBox.setSelected(active.get(PythonKeywords.CASE));
        instanceofCheckBox.setSelected(active.get(PythonKeywords.INSTANCEOF));
        returnCheckBox.setSelected(active.get(PythonKeywords.RETURN));
        transientCheckBox.setSelected(active.get(PythonKeywords.TRANSIENT));
        catchCheckBox.setSelected(active.get(PythonKeywords.CATCH));
        intCheckBox.setSelected(active.get(PythonKeywords.INT));
        shortCheckBox.setSelected(active.get(PythonKeywords.SHORT));
        tryCheckBox.setSelected(active.get(PythonKeywords.TRY));
        charCheckBox.setSelected(active.get(PythonKeywords.CHAR));
        finalCheckBox.setSelected(active.get(PythonKeywords.FINAL));
        finallyCheckBox.setSelected(active.get(PythonKeywords.FINALLY));
        longCheckBox.setSelected(active.get(PythonKeywords.LONG));
        strictfpCheckBox.setSelected(active.get(PythonKeywords.STRICTFP));
        floatCheckBox.setSelected(active.get(PythonKeywords.FLOAT));
        superCheckBox.setSelected(active.get(PythonKeywords.SUPER));
        whileCheckBox.setSelected(active.get(PythonKeywords.WHILE));
    }

    public void setKeywordTotalSubmetric(boolean enabled, boolean required) {
        totalKeywordCountInCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox.setEnabled(enabled);
        requiredSubmetricCheckBox.setSelected(required);
    }
    public void setKeywordDensitySubmetric(boolean enabled, boolean required) {
        keywordDensityPerLineCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox1.setEnabled(enabled);
        requiredSubmetricCheckBox1.setSelected(required);
    }

    public void setCouplingTotalSubmetric(boolean enabled, boolean required) {
        totalConnectivityInSegmentCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox2.setEnabled(enabled);
        requiredSubmetricCheckBox2.setSelected(required);
    }
    public void setCouplingDensitySubmetric(boolean enabled, boolean required) {
        connectivityDensityPerLineCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox3.setEnabled(enabled);
        requiredSubmetricCheckBox3.setSelected(required);
    }
    public void setTotalConnectivity(boolean enabled, boolean required) {
        useTotalConnectivityCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox4.setEnabled(enabled);
        requiredSubmetricCheckBox4.setSelected(required);
    }
    public void setFieldConnectivity(boolean enabled, boolean required) {
        useFieldConnectivityCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox5.setEnabled(enabled);
        requiredSubmetricCheckBox5.setSelected(required);
    }
    public void setMethodConnectivity(boolean enabled, boolean required) {
        useMethodConnectivityCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox6.setEnabled(enabled);
        requiredSubmetricCheckBox6.setSelected(required);
    }

    public void setComplexityTotalSubmetric(boolean enabled, boolean required) {
        totalComplexityOfSegmentCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox7.setEnabled(enabled);
        requiredSubmetricCheckBox7.setSelected(required);
    }
    public void setComplexityDensitySubmetric(boolean enabled, boolean required) {
        complexityDensityPerLineCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox8.setEnabled(enabled);
        requiredSubmetricCheckBox8.setSelected(required);
    }
    public void setMethodDeclarationArea(boolean enabled, boolean required) {
        methodDeclarationAreaCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox11.setEnabled(enabled);
        requiredSubmetricCheckBox11.setSelected(required);
    }
    public void setMethodDeclarationDepthDensity(boolean enabled, boolean required) {
        methodDeclarationDepthDensityCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox12.setEnabled(enabled);
        requiredSubmetricCheckBox12.setSelected(required);
    }

    public void setSizeByLinesSubmetric(boolean enabled, boolean required) {
        numberOfLinesInCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox9.setEnabled(enabled);
        requiredSubmetricCheckBox9.setSelected(required);
    }
    public void setSizeBySymbolsSubmetric(boolean enabled, boolean required) {
        numberOfSymbolsInCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox10.setEnabled(enabled);
        requiredSubmetricCheckBox10.setSelected(required);
    }
    public void setSizeByDensityOfSymbolsSubmetric(boolean enabled, boolean required) {
        densityOfSymbolsInCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox13.setEnabled(enabled);
        requiredSubmetricCheckBox13.setSelected(required);
    }
    public void setMeasureSizeOfSegment(boolean enabled, boolean required) {
        measureSizeOfSegmentCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox14.setEnabled(enabled);
        requiredSubmetricCheckBox14.setSelected(required);
    }
    public void setMeasureSizeOfMethod(boolean enabled, boolean required) {
        measureSizeOfMethodCheckBox.setSelected(enabled);
        requiredSubmetricCheckBox15.setEnabled(enabled);
        requiredSubmetricCheckBox15.setSelected(required);
    }


    public Boolean[] getKeywordTotalSubmetricInfo() { return new Boolean[] {totalKeywordCountInCheckBox.isSelected(), requiredSubmetricCheckBox.isSelected()}; }
    public Boolean[] getKeywordDensitySubmetricInfo() { return new Boolean[] {keywordDensityPerLineCheckBox.isSelected(), requiredSubmetricCheckBox1.isSelected()}; }
    public EnumMap<PythonKeywords, Boolean> getActiveKeywords() {
        EnumMap<PythonKeywords, Boolean> activeKeywords = new EnumMap<>(PythonKeywords.class);

        activeKeywords.put(PythonKeywords.CONTINUE, continueCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.FOR, forCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.NEW, newCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.SWITCH, switchCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.ASSERT, assertCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.SYNCHRONIZED, synchronizedCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.BOOLEAN, booleanCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.DO, doCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.IF, ifCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.THIS, thisCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.BREAK, breakCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.DOUBLE, doubleCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.THROW, throwCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.BYTE, byteCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.ELSE, elseCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.CASE, caseCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.INSTANCEOF, instanceofCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.RETURN, returnCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.TRANSIENT, transientCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.CATCH, catchCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.INT, intCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.SHORT, shortCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.TRY, tryCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.CHAR, charCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.FINAL, finalCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.FINALLY, finallyCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.LONG, longCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.STRICTFP, strictfpCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.FLOAT, floatCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.SUPER, superCheckBox.isSelected());
        activeKeywords.put(PythonKeywords.WHILE, whileCheckBox.isSelected());

        return activeKeywords;
    }

    public Boolean[] getCouplingTotalSubmetricInfo() { return new Boolean[] {totalConnectivityInSegmentCheckBox.isSelected(), requiredSubmetricCheckBox2.isSelected()}; }
    public Boolean[] getCouplingDensitySubmetricInfo() { return new Boolean[] {connectivityDensityPerLineCheckBox.isSelected(), requiredSubmetricCheckBox3.isSelected()}; }
    public Boolean[] getTotalConnectivityInfo() { return new Boolean[] {useTotalConnectivityCheckBox.isSelected(), requiredSubmetricCheckBox4.isSelected()}; }
    public Boolean[] getFieldConnectivityInfo() { return new Boolean[] {useFieldConnectivityCheckBox.isSelected(), requiredSubmetricCheckBox5.isSelected()}; }
    public Boolean[] getMethodConnectivityInfo() { return new Boolean[] {useMethodConnectivityCheckBox.isSelected(), requiredSubmetricCheckBox6.isSelected()}; }

    public Boolean[] getComplexityTotalSubmetricInfo() { return new Boolean[] {totalComplexityOfSegmentCheckBox.isSelected(), requiredSubmetricCheckBox7.isSelected()}; }
    public Boolean[] getComplexityDensitySubmetricInfo() { return new Boolean[] {complexityDensityPerLineCheckBox.isSelected(), requiredSubmetricCheckBox8.isSelected()}; }
    public Boolean[] getComplexityMethodAreaInfo() { return new Boolean[] {methodDeclarationAreaCheckBox.isSelected(), requiredSubmetricCheckBox11.isSelected()}; }
    public Boolean[] getComplexityMethodDensityInfo() { return new Boolean[] {methodDeclarationDepthDensityCheckBox.isSelected(), requiredSubmetricCheckBox12.isSelected()}; }

    public Boolean[] getSizeByLinesSubmetricInfo() { return new Boolean[] {numberOfLinesInCheckBox.isSelected(), requiredSubmetricCheckBox9.isSelected()}; }
    public Boolean[] getSizeBySymbolsSubmetricInfo() { return new Boolean[] {numberOfSymbolsInCheckBox.isSelected(), requiredSubmetricCheckBox10.isSelected()}; }
    public Boolean[] getSizeBySymbolDensitySubmetricInfo() { return new Boolean[] {densityOfSymbolsInCheckBox.isSelected(), requiredSubmetricCheckBox13.isSelected()}; }
    public Boolean[] getMeasureSizeOfSegmentInfo() { return new Boolean[] {measureSizeOfSegmentCheckBox.isSelected(), requiredSubmetricCheckBox14.isSelected()}; }
    public Boolean[] getMeasureSizeOfMethodInfo() { return new Boolean[] {measureSizeOfMethodCheckBox.isSelected(), requiredSubmetricCheckBox15.isSelected()}; }
}
