package org.jetbrains.research.anticopypasterpython.config;

import javax.swing.*;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import org.jetbrains.research.anticopypasterpython.config.advanced.AdvancedProjectSettingsDialogWrapper;
import org.jetbrains.research.anticopypasterpython.config.credentials.CredentialsDialogWrapper;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ProjectSettingsComponent {

    private JPanel mainPanel;
    private JSlider keywordsSlider;
    private JCheckBox keywordsEnabledCheckBox;
    private JCheckBox keywordsRequiredCheckBox;
    private JSlider couplingSlider;
    private JCheckBox couplingEnabledCheckBox;
    private JCheckBox couplingRequiredCheckBox;
    private JSlider sizeSlider;
    private JCheckBox sizeEnabledCheckBox;
    private JCheckBox sizeRequiredCheckBox;
    private JSlider complexitySlider;
    private JCheckBox complexityEnabledCheckBox;
    private JCheckBox complexityRequiredCheckBox;
    private JButton advancedSettingsButton;
    private JSpinner minimumMethodSelector;
    private JSpinner timeBufferSelector;
    private JButton statisticsCollectionButton;
    private JLabel helpLabel;
    private JLabel duplicateMethodsHelp;
    private JLabel waitTimeHelp;
    private JLabel statisticsButtonHelp;
    private JLabel advancedButtonHelp;
    private JCheckBox tensorFlowModel;
    private JLabel tensorFlowModelHelp;

    private JCheckBox highlight;
    private JLabel generalPrefHelp;
    private JSpinner highLightTimerSelector;
    private JLabel highlightTimer;
    private static final Logger LOG = Logger.getInstance(ProjectSettingsComponent.class);

    public ProjectSettingsComponent(Project project) {

        // Set listeners for buttons
        advancedSettingsButton.addActionListener(e -> {
            AdvancedProjectSettingsDialogWrapper advancedDialog = new AdvancedProjectSettingsDialogWrapper(project);
            boolean displayAndResolveAdvanced = advancedDialog.showAndGet();
            advancedDialog.saveSettings(displayAndResolveAdvanced);
        });
        statisticsCollectionButton.addActionListener(e -> {
            CredentialsDialogWrapper credentialsDialog = new CredentialsDialogWrapper(project);
            boolean displayAndResolveCredentials = credentialsDialog.showAndGet();
            credentialsDialog.saveSettings(displayAndResolveCredentials);
        });

        tensorFlowModel.addActionListener(e -> {
            System.out.println("TensorFlow Enabled: " + tensorFlowModel.isSelected());
        });

        addConditionallyEnabledMetricGroup(keywordsEnabledCheckBox,keywordsSlider,keywordsRequiredCheckBox);
        addConditionallyEnabledMetricGroup(couplingEnabledCheckBox,couplingSlider,couplingRequiredCheckBox);
        addConditionallyEnabledMetricGroup(complexityEnabledCheckBox, complexitySlider, complexityRequiredCheckBox);
        addConditionallyEnabledMetricGroup(sizeEnabledCheckBox, sizeSlider, sizeRequiredCheckBox);

        createUIComponents();
    }

    private void addConditionallyEnabledMetricGroup(JCheckBox ind, JSlider depslid, JCheckBox dep) {
        ind.addActionListener(e -> {
                    if (ind.isSelected()) {
                        dep.setEnabled(true);
                        depslid.setEnabled(true);
                        dep.setSelected(true);
                    } else {
                        dep.setSelected(false);
                        depslid.setEnabled(false);
                        dep.setEnabled(false);
                    }
                }
        );
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return minimumMethodSelector;
    }

    public int getMinimumDuplicateMethods() {if((int)minimumMethodSelector.getValue() < 0){return 0;} else{return (int) minimumMethodSelector.getValue();} }

    public void setMinimumDuplicateMethods(int minimumMethods) { if(minimumMethods < 0){minimumMethods = 0;} minimumMethodSelector.setValue(minimumMethods); }

    public int getHighlightTimerSelector() {if((int)highLightTimerSelector.getValue() < 0){return 0;} else{return (int) highLightTimerSelector.getValue();} }

    public void setHighLightTimerSelector(int highlightDelay) { if(highlightDelay < 0){highlightDelay = 0;} highLightTimerSelector.setValue(highlightDelay); }

    public int getTimeBuffer() { if((int)timeBufferSelector.getValue() < 0){return 0;} else{return (int) timeBufferSelector.getValue();} }

    public void setTimeBuffer(int timeBuffer){ if(timeBuffer < 0){timeBuffer = 0;} timeBufferSelector.setValue(timeBuffer); }

    public int getKeywordsSensitivity() {
        return keywordsSlider.getValue();
    }

    public boolean getHighlight() { return highlight.isSelected();}

    public void setHighlight(boolean enabled) { highlight.setSelected(enabled);}
    public void setKeywordsSensitivity(int sensitivity) {
        keywordsSlider.setValue(sensitivity);
    }

    public boolean getKeywordsEnabled() {
        return keywordsEnabledCheckBox.isSelected();
    }

    public void setKeywordsEnabled(boolean enabled) {
        keywordsEnabledCheckBox.setSelected(enabled);
    }

    public boolean getKeywordsRequired() {
        return keywordsRequiredCheckBox.isSelected();
    }

    public void setKeywordsRequired(boolean required) {
        keywordsRequiredCheckBox.setSelected(required);
    }

    public int getCouplingSensitivity() {
        return couplingSlider.getValue();
    }

    public void setCouplingSensitivity(int sensitivity) {
        couplingSlider.setValue(sensitivity);
    }

    public boolean getCouplingEnabled() {
        return couplingEnabledCheckBox.isSelected();
    }

    public void setCouplingEnabled(boolean enabled) {
        couplingEnabledCheckBox.setSelected(enabled);
    }

    public boolean getCouplingRequired() {
        return couplingRequiredCheckBox.isSelected();
    }

    public void setCouplingRequired(boolean required) {
        couplingRequiredCheckBox.setSelected(required);
    }

    public int getSizeSensitivity() {
        return sizeSlider.getValue();
    }

    public void setSizeSensitivity(int sensitivity) {
        sizeSlider.setValue(sensitivity);
    }

    public boolean getSizeEnabled() {
        return sizeEnabledCheckBox.isSelected();
    }

    public void setSizeEnabled(boolean enabled) {
        sizeEnabledCheckBox.setSelected(enabled);
    }

    public boolean getSizeRequired() {
        return sizeRequiredCheckBox.isSelected();
    }

    public void setSizeRequired(boolean required) {
        sizeRequiredCheckBox.setSelected(required);
    }

    public int getComplexitySensitivity() {
        return complexitySlider.getValue();
    }

    public void setComplexitySensitivity(int sensitivity) {
        complexitySlider.setValue(sensitivity);
    }

    public boolean getComplexityEnabled() {
        return complexityEnabledCheckBox.isSelected();
    }

    public void setComplexityEnabled(boolean enabled) {
        complexityEnabledCheckBox.setSelected(enabled);
    }

    public boolean getComplexityRequired() {
        return complexityRequiredCheckBox.isSelected();
    }

    public void setComplexityRequired(boolean required) {
        complexityRequiredCheckBox.setSelected(required);
    }

    public boolean getTensorFlowModelEnabled(){return tensorFlowModel.isSelected();}
    public void setTensorFlowModelEnabled(boolean enabled) { tensorFlowModel.setSelected(enabled);}

    private void createUIComponents() {
        // Set link and icons for help features
        helpLabel = new JLabel();
        createLinkListener(helpLabel, "https://se4airesearch.github.io/AntiCopyPaster_Python_Website_Fall2023/");
        helpLabel.setIcon(AllIcons.Ide.External_link_arrow);
        duplicateMethodsHelp = new JLabel();
        duplicateMethodsHelp.setIcon(AllIcons.General.ContextHelp);
        highlightTimer = new JLabel();
        highlightTimer.setIcon(AllIcons.General.ContextHelp);
        waitTimeHelp = new JLabel();
        waitTimeHelp.setIcon(AllIcons.General.ContextHelp);
        generalPrefHelp = new JLabel();
        generalPrefHelp.setIcon(AllIcons.General.ContextHelp);
        tensorFlowModelHelp = new JLabel();
        tensorFlowModelHelp.setIcon(AllIcons.General.ContextHelp);
        advancedButtonHelp = new JLabel();
        advancedButtonHelp.setIcon(AllIcons.General.ContextHelp);
        statisticsButtonHelp = new JLabel();
        statisticsButtonHelp.setIcon(AllIcons.General.ContextHelp);
    }

    public static void createLinkListener(JComponent component, String url) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    URI uri = new URI(url);
                    Desktop.getDesktop().browse(uri);
                } catch (IOException | URISyntaxException ex) {
                    LOG.error("Failed to open link", ex);
                }
            }
        });
    }
}
