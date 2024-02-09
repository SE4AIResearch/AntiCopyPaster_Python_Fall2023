package org.jetbrains.research.anticopypasterpython.config.advanced;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;

import static org.jetbrains.research.anticopypasterpython.config.ProjectSettingsComponent.createLinkListener;
import java.util.EnumMap;

public class AdvancedProjectSettingsComponent {
    private JCheckBox totalKeywordCountInCheckBox;
    private JCheckBox requiredSubmetricCheckBox;
    private JCheckBox keywordDensityPerLineCheckBox;
    private JCheckBox requiredSubmetricCheckBox1;
    private JCheckBox falseCheckBox;
    private JCheckBox forCheckBox;
    private JCheckBox trueCheckBox;
    private JCheckBox andCheckBox;
    private JCheckBox asCheckBox;
    private JCheckBox classCheckBox;
    private JCheckBox delCheckBox;
    private JCheckBox importCheckBox;
    private JCheckBox nonlocalCheckBox;
    private JCheckBox passCheckBox;
    private JCheckBox assertCheckBox;
    private JCheckBox continueCheckBox;
    private JCheckBox elifCheckBox;
    private JCheckBox fromCheckBox;
    private JCheckBox inCheckBox;
    private JCheckBox finallyCheckBox;
    private JCheckBox raiseCheckBox;
    private JCheckBox returnCheckBox;
    private JCheckBox notCheckBox;
    private JCheckBox orCheckBox;
    private JCheckBox isCheckBox;
    private JCheckBox lambdaCheckBox;
    private JCheckBox ifCheckBox;
    private JCheckBox globalCheckBox;
    private JCheckBox exceptCheckBox;
    private JCheckBox elseCheckBox;
    private JCheckBox defCheckBox;
    private JCheckBox breakCheckBox;
    private JCheckBox awaitCheckBox;
    private JCheckBox asyncCheckBox;
    private JCheckBox tryCheckBox;
    private JCheckBox whileCheckBox;
    private JCheckBox withCheckBox;
    private JCheckBox yieldCheckBox;

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
            EnumMap<JavaKeywords, Boolean> allTrueMap = new EnumMap<>(JavaKeywords.class);
            for (JavaKeywords keyword : JavaKeywords.values()) { allTrueMap.put(keyword, true); }
            setActiveKeywords(allTrueMap);
        });
        selectNoneButton.addActionListener(e -> {
            EnumMap<JavaKeywords, Boolean> allFalseMap = new EnumMap<>(JavaKeywords.class);
            for (JavaKeywords keyword : JavaKeywords.values()) { allFalseMap.put(keyword, false); }
            setActiveKeywords(allFalseMap);
        });

        // Initialize main help interface
        helpLabel = new JLabel();
        createLinkListener(helpLabel, "https://se4airesearch.github.io/AntiCopyPaster_Summer2023/pages/metric.html");
        helpLabel.setIcon(AllIcons.Ide.External_link_arrow);

        enabledKeywordsHelp = new JLabel();
        enabledKeywordsHelp.setIcon(AllIcons.General.ContextHelp);
    }

    public enum JavaKeywords {
        FALSE, NONE, TRUE, AND, AS, ASSERT, ASYNC, AWAIT, BREAK, CLASS, CONTINUE, DEF, DEL, ELIF, ELSE,
        EXCEPT, FINALLY, FOR, FROM, GLOBAL, IF, IMPORT, IN, IS, LAMBDA, NONLOCAL, NOT, OR, PASS, RAISE, RETURN,
        TRY, WHILE, WITH, YIELD
    }

    public void setActiveKeywords(EnumMap<JavaKeywords, Boolean> active) {
        falseCheckBox.setSelected(active.get(JavaKeywords.FALSE));
        forCheckBox.setSelected(active.get(JavaKeywords.NONE));
        trueCheckBox.setSelected(active.get(JavaKeywords.TRUE));
        andCheckBox.setSelected(active.get(JavaKeywords.AND));
        asCheckBox.setSelected(active.get(JavaKeywords.AS));
        assertCheckBox.setSelected(active.get(JavaKeywords.ASSERT));
        asyncCheckBox.setSelected(active.get(JavaKeywords.ASYNC));
        awaitCheckBox.setSelected(active.get(JavaKeywords.AWAIT));
        breakCheckBox.setSelected(active.get(JavaKeywords.BREAK));
        classCheckBox.setSelected(active.get(JavaKeywords.CLASS));
        continueCheckBox.setSelected(active.get(JavaKeywords.CONTINUE));
        defCheckBox.setSelected(active.get(JavaKeywords.DEF));
        delCheckBox.setSelected(active.get(JavaKeywords.DEL));
        elifCheckBox.setSelected(active.get(JavaKeywords.ELIF));
        elseCheckBox.setSelected(active.get(JavaKeywords.ELSE));
        exceptCheckBox.setSelected(active.get(JavaKeywords.EXCEPT));
        finallyCheckBox.setSelected(active.get(JavaKeywords.FINALLY));
        forCheckBox.setSelected(active.get(JavaKeywords.FOR));
        fromCheckBox.setSelected(active.get(JavaKeywords.FROM));
        globalCheckBox.setSelected(active.get(JavaKeywords.GLOBAL));
        ifCheckBox.setSelected(active.get(JavaKeywords.IF));
        importCheckBox.setSelected(active.get(JavaKeywords.IMPORT));
        inCheckBox.setSelected(active.get(JavaKeywords.IN));
        isCheckBox.setSelected(active.get(JavaKeywords.IS));
        lambdaCheckBox.setSelected(active.get(JavaKeywords.LAMBDA));
        nonlocalCheckBox.setSelected(active.get(JavaKeywords.NONLOCAL));
        notCheckBox.setSelected(active.get(JavaKeywords.NOT));
        orCheckBox.setSelected(active.get(JavaKeywords.OR));
        passCheckBox.setSelected(active.get(JavaKeywords.PASS));
        raiseCheckBox.setSelected(active.get(JavaKeywords.RAISE));
        returnCheckBox.setSelected(active.get(JavaKeywords.RETURN));
        tryCheckBox.setSelected(active.get(JavaKeywords.TRY));
        whileCheckBox.setSelected(active.get(JavaKeywords.WHILE));
        withCheckBox.setSelected(active.get(JavaKeywords.WITH));
        yieldCheckBox.setSelected(active.get(JavaKeywords.YIELD));
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
    public EnumMap<JavaKeywords, Boolean> getActiveKeywords() {
        EnumMap<JavaKeywords, Boolean> activeKeywords = new EnumMap<>(JavaKeywords.class);

        activeKeywords.put(JavaKeywords.FALSE, falseCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.NONE, forCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.TRUE, trueCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.AND, andCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.AS, asCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.ASSERT, assertCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.ASYNC, asyncCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.AWAIT, awaitCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.CLASS, classCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.CONTINUE, continueCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.BREAK, breakCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.DEF, defCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.DEL, delCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.ELIF, elifCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.ELSE, elseCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.EXCEPT, exceptCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.FOR, forCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.FROM, fromCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.GLOBAL, globalCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.IF, ifCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.IMPORT, importCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.IN, inCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.IS, isCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.LAMBDA, lambdaCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.NONLOCAL, nonlocalCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.FINALLY, finallyCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.NOT, notCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.OR, orCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.PASS, passCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RAISE, raiseCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RETURN, returnCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RETURN, returnCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RETURN, returnCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RETURN, returnCheckBox.isSelected());
        activeKeywords.put(JavaKeywords.RETURN, returnCheckBox.isSelected());

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
