package org.jetbrains.research.anticopypasterpython.utils;

import com.jetbrains.python.PythonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.python.psi.PyFunction;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.research.anticopypasterpython.metrics.MetricCalculator;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;

import com.jetbrains.python.psi.PyFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used to gather metrics from every method within the currently
 * open IntelliJ Project, provided on declaration.
 */
public class MetricsGatherer {
    /**
     * A list of all the FeaturesVectors for all methods within
     * the IntelliJ Project.
     */
    private List<FeaturesVector> methodsMetrics;
    private Project project;

    /**
     * Builds an instance of the MetricsGatherer and
     * does the initial (and only) metrics gathering.
     */
    public MetricsGatherer(Project project){
        this.methodsMetrics = new ArrayList<>();
        this.project = project;
        gatherMetrics();
    }

    /**
     * Getter for the methodsMetrics.
     * @return the list of featuresVectors made by the gatherer.
     */
    public List<FeaturesVector> getMethodsMetrics() {
        return this.methodsMetrics;
    }
    /**
     * Gathers all the metrics from every method within the open IntelliJ Project.
     */
    private void gatherMetrics() {
        var vfCollectionWrapper = new Object(){ Collection<VirtualFile> vfCollection = null; };
        ApplicationManager.getApplication().runReadAction(() -> {
            // Gets all Java files from the Project
            vfCollectionWrapper.vfCollection = FileTypeIndex.getFiles(
                    PythonFileType.INSTANCE,
                    GlobalSearchScope.projectScope(project));
        });
        Collection<VirtualFile> vfCollection = vfCollectionWrapper.vfCollection;
        // Gets a PsiFile for every Java file in the project
        List<PyFile> pfList = new ArrayList<>();
        for (VirtualFile file : vfCollection) {
            // Makes everything lowercase for consistency, and gets rid of file extension
            String filename = file.getName().toLowerCase().split("[.]")[0];
            if (!filename.startsWith("test") && !filename.endsWith("test")) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    pfList.add((PyFile) PsiManager.getInstance(project).findFile(file));
                });

            }
        }

        // Gets all the PsiMethods, as well as their start and end lines.
        for (PyFile psiFile: pfList) {
            System.out.println(psiFile.getName());
            // wrappers are used to get information out of runReadActions.
            // PsiTree's can't be accessed outside a read action, or it
            // can cause race conditions.
            var psiMethodWrapper = new Object() {
                Collection<PyFunction> psiMethods = null;
            };
            ApplicationManager.getApplication().runReadAction(() -> {
                psiMethodWrapper.psiMethods = PsiTreeUtil.findChildrenOfType(psiFile, PyFunction.class);
            });

            Collection<PyFunction> psiMethods = psiMethodWrapper.psiMethods;
            for (PyFunction method : psiMethods) {
                int startLine = PsiUtil.getNumberOfLine(psiFile, method.getTextRange().getStartOffset());
                int endLine = PsiUtil.getNumberOfLine(psiFile, method.getTextRange().getEndOffset());

                var fvWrapper = new Object() {
                    FeaturesVector features = null;
                };
                ApplicationManager.getApplication().runReadAction(() -> {
                    fvWrapper.features = new
                            MetricCalculator(method, method.getText(), startLine, endLine).getFeaturesVector();
                });

                FeaturesVector features = fvWrapper.features;
                this.methodsMetrics.add(features);
            }
        }
        System.out.println("broke free");
    }


    public void setProject(Project project) {
        this.project = project;
    }
}
