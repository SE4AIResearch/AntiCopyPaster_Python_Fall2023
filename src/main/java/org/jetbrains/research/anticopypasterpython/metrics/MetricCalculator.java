package org.jetbrains.research.anticopypasterpython.metrics;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
//import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFunction;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.anticopypasterpython.metrics.features.Feature;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeatureItem;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;
import org.jetbrains.research.anticopypasterpython.metrics.utils.MemberSets;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.jetbrains.research.anticopypasterpython.metrics.utils.DepthAnalyzer.getNestingArea;

public class MetricCalculator {
    private final String statementsStr;
    private final PyFunction method;

    private final int beginLine;
    private final int endLine;
    private final FeaturesVector featuresVector;

    public MetricCalculator(PyFunction dummyPsiMethod, String code, int beginLine, int endLine) {
        this.method = dummyPsiMethod;
        this.statementsStr = code;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.featuresVector = new FeaturesVector(78); // TODO: Make dimension changeable outside
        computeFeatureVector();
    }

    public static void writeFeaturesToFile(PyFunction dummyPsiMethod, String code,
                                           int beginLine, int endLine, FileWriter fileWriter) throws IOException {
        MetricCalculator metricCalculator =
                new MetricCalculator(dummyPsiMethod, code, beginLine, endLine);
        FeaturesVector featuresVector = metricCalculator.getFeaturesVector();

        for (int i = 0; i < featuresVector.getDimension(); i++) {
            fileWriter.append(String.valueOf(featuresVector.getFeatureValue(Feature.fromId(i))));
            fileWriter.append(';');
        }
    }

    private void computeFeatureVector() {
        couplingFeatures();
        keywordFeatures();
        methodFeatures();
        metaFeatures();
    }

    public FeaturesVector getFeaturesVector() {
        return this.featuresVector;
    }

    private void couplingFeatures() {
        PsiFile thisFile = method.getContainingFile();
        MemberSets memberSets = MemberSets.extractAllMethodsAndFields(thisFile);

        int linesCount = endLine - beginLine + 1;

        int fieldMatches = 0;
        int methodMatches = 0;
        int totalMatches;

        PsiFileFactory factory = PsiFileFactory.getInstance(thisFile.getProject());
        @Nullable PsiFile psiFromText = factory.createFileFromText(statementsStr, thisFile);
        // search for all identifiers (methods and variables) in the code fragment
        @NotNull Collection<PyFunction> identifiers = PsiTreeUtil.collectElementsOfType(psiFromText,
                PyFunction.class); //<Need to verify psidentifer can be replaced by pyfunction>
        HashSet<String> identifiersNames = new HashSet<>();
        identifiers.forEach(i -> identifiersNames.add(i.getText()));

        for (String fieldName : memberSets.fields) {
            if (identifiersNames.contains(fieldName)) {
                fieldMatches += 1;
            }
        }

        for (String methodName : memberSets.methods) {
            if (identifiersNames.contains(methodName)) {
                methodMatches += 1;
            }
        }

        totalMatches = methodMatches + fieldMatches;

        featuresVector.setFeature(new FeatureItem(Feature.TotalConnectivity, totalMatches));
        featuresVector.setFeature(new FeatureItem(
                Feature.TotalConnectivityPerLine, (double) totalMatches / linesCount));
        featuresVector.setFeature(new FeatureItem(Feature.FieldConnectivity, fieldMatches));
        featuresVector.setFeature(new FeatureItem(
                Feature.FieldConnectivityPerLine, (double) fieldMatches / linesCount));
        featuresVector.setFeature(new FeatureItem(Feature.MethodConnectivity, methodMatches));
        featuresVector.setFeature(new FeatureItem(
                Feature.MethodConnectivityPerLine, (double) methodMatches / linesCount));
    }

    private void keywordFeatures() {
//        List<String> allKeywords = Arrays.asList(
//                "continue", "for", "new", "switch",
//                "assert", "synchronized", "boolean", "do",
//                "if", "this", "break", "double",
//                "throw", "byte", "else", "case",
//                "instanceof", "return", "transient",
//                "catch", "int", "short", "try",
//                "char", "final", "finally", "long",
//                "strictfp", "float", "super", "while"); // 31 keywords, id from 20 to 81

        List<String> allKeywords = Arrays.asList(
            "and", "as", "assert", "break", "class", "continue", "def", "del",
            "elif", "else", "except", "False", "finally", "for", "from", "global",
            "if", "import", "in", "is", "lambda", "None", "nonlocal", "not", "or",
            "pass", "raise", "return", "True", "try", "while", "with", "yield"
        );

        HashMap<String, Integer> counts = new HashMap<>();
        for (String key : allKeywords) {
            counts.put(key, StringUtils.countMatches(this.statementsStr, key));
        }

        int linesCount = endLine - beginLine + 1;

        int id = 16; // initialized with 16 to account for shift in Keyword-Features begin id.
        for (String keyword : allKeywords) {
            Integer count = counts.get(keyword);
            featuresVector.setFeature(new FeatureItem(Feature.fromId(id++), count));
            featuresVector.setFeature(new FeatureItem(Feature.fromId(id++), (double) count / linesCount));
        }
    }

    private void methodFeatures() {
        String methodStr = this.method.getText();
        int methodArea = getNestingArea(methodStr);
        int lineCount = StringUtils.countMatches(methodStr, '\n') + 1;

        featuresVector.setFeature(new FeatureItem(Feature.MethodDeclarationLines, lineCount));
        featuresVector.setFeature(new FeatureItem(Feature.MethodDeclarationSymbols, methodStr.length()));
        featuresVector.setFeature(new FeatureItem(
                Feature.MethodDeclarationSymbolsPerLine, (double) methodStr.length() / lineCount));
        featuresVector.setFeature(new FeatureItem(Feature.MethodDeclarationArea, methodArea));
        featuresVector.setFeature(new FeatureItem(
                Feature.MethodDeclarationAreaPerLine, (double) methodArea / lineCount));

    }

    private void metaFeatures() {
        String fragment = statementsStr;
        int fragmentArea = getNestingArea(fragment);
        int lineCount = StringUtils.countMatches(fragment, '\n') + 1;

        featuresVector.setFeature(new FeatureItem(Feature.TotalLinesOfCode, lineCount));
        featuresVector.setFeature(new FeatureItem(Feature.TotalSymbols, fragment.length()));
        featuresVector.setFeature(new FeatureItem(
                Feature.SymbolsPerLine, (double) fragment.length() / lineCount));
        featuresVector.setFeature(new FeatureItem(Feature.Area, fragmentArea));
        featuresVector.setFeature(new FeatureItem(
                Feature.AreaPerLine, (double) fragmentArea / lineCount));

    }
}