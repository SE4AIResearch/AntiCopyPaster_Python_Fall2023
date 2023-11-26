package org.jetbrains.research.anticopypasterpython.utils;

//import com.intellij.codeInsight.CodeInsightUtil;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.refactoring.IntroduceVariableUtil;
//import com.intellij.util.CommonJavaRefactoringUtil;
import com.jetbrains.python.psi.PyFunction;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParameterList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.types.PyType;


import com.jetbrains.python.PythonFileType;

import java.util.Objects;
import java.util.regex.Pattern;

public class PsiUtil {

    private static final Logger LOG = Logger.getInstance(PsiUtil.class);

    /**
     * Check the before revision of the file (without local changes) to find the original method.
     *
     * @param fileWithLocalChanges file that contains the local changes;
     * @param method               method to search for;
     * @return original method.
     */
    public static PyFunction getMethodStartLineInBeforeRevision(PsiFile fileWithLocalChanges, PyFunction method) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(fileWithLocalChanges.getProject());
        Change change = changeListManager.getChange(fileWithLocalChanges.getVirtualFile());
        if (change != null) {
            ContentRevision beforeRevision = change.getBeforeRevision();
            if (beforeRevision != null) {
                try {
                    String content = beforeRevision.getContent();
                    if (content != null) {
                        PsiFile psiFileBeforeRevision =
                                PsiFileFactory.getInstance(fileWithLocalChanges.getProject()).createFileFromText("tmp",
                                        PythonFileType.INSTANCE,
                                        content);
                        PsiElement[] children = psiFileBeforeRevision.getChildren();
                        for (PsiElement element : children) {
                            if (element instanceof PyClass) {
                                PyClass psiClass = (PyClass) element;
                                PyFunction[] methods = psiClass.getMethods();
                                for (PyFunction pyFunction : methods) {
                                    if (equalSignatures(method, pyFunction)) {
                                        return pyFunction;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("[ACP] Failed to get a file's content from the last revision.", e.getMessage());
                }
            }
        }
        return null;
    }

    public static int getNumberOfLine(PsiFile file, int offset) {
        FileViewProvider fileViewProvider = file.getViewProvider();
        Document document = fileViewProvider.getDocument();
        return document != null ? document.getLineNumber(offset) + 1 : 0;
    }

    public static boolean equalSignatures(PyFunction method1, PyFunction method2) {
        return Objects.equals(calculateSignature(method1), calculateSignature(method2));
    }

    public static String calculateSignature(PyFunction method) {
        final PyClass containingClass = method.getContainingClass();
        final String className;
        if (containingClass != null) {
            className = containingClass.getQualifiedName();
        } else {
            className = "";
        }
        final String methodName = method.getName();
        final StringBuilder out = new StringBuilder(50);
        out.append(className);
        out.append("::");
        out.append(methodName);
        out.append('(');
        final PyParameterList parameterList = method.getParameterList();
        final PyParameter[] parameters = parameterList.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (i != 0) {
                out.append(',');
            }
            final PyType parameterType = parameters[i].getType();
            final String parameterTypeText = parameterType.getPresentableText();
            out.append(parameterTypeText);
        }
        out.append(')');
        return out.toString();
    }

    public static PyFunction findMethodByOffset(PsiFile psiFile, int offset) {
        PsiElement element = psiFile.findElementAt(offset);

        return (PyFunction) PsiTreeUtil.findFirstParent(element, p -> p instanceof PyFunction);

    }

    public static PyElement[] getElements(@NotNull Project project, @NotNull PsiFile file,
                                           int startOffset, int endOffset) {
        PyElement[] elements;
        PyExpression expr = CodeInsightUtil.findExpressionInRange(file, startOffset, endOffset);
        if (expr != null) {
            elements = new PyElement[]{expr};
        } else {
            elements = CodeInsightUtil.findStatementsInRange(file, startOffset, endOffset);
            if (elements.length == 0) {
                final PyExpression expression =
                        IntroduceVariableUtil.getSelectedExpression(project, file, startOffset, endOffset);
                if (expression != null && IntroduceVariableUtil.getErrorMessage(expression) == null) {
                    final PsiType originalType = CommonJavaRefactoringUtil.getTypeByExpressionWithExpectedType(expression);
                    if (originalType != null) {
                        elements = new PyElement[]{expression};
                    }
                }
            }
        }
        return elements;
    }

    public static int getStartOffset(Editor editor, PsiFile file, String text) {
        int caretPos = editor.getCaretModel().getOffset();
        String fileText = file.getText();
        int best_dist = 1000000000;
        int startOffset = -1;

        int fromIdx = 0;
        while (true) {
            int idx = fileText.indexOf(text, fromIdx);
            fromIdx = idx + 1;

            if (idx == -1) {
                break;
            }

            int dist = Math.abs(idx - caretPos) + Math.abs(idx + text.length() - 1 - caretPos);
            if (dist < best_dist) {
                best_dist = dist;
                startOffset = idx;
            }
        }
        return startOffset;
    }

    public static int getCountOfCodeLines(String text) {
        Pattern p = Pattern.compile("/\\*[\\s\\S]*?\\*/");
        java.util.regex.Matcher m = p.matcher(text);
        int totalCountWithComment = 0;
        int rawLocs = StringUtils.countMatches(text, "\n") + 1;
        while (m.find()) {
            String[] lines = m.group(0).split("\n");
            totalCountWithComment += lines.length;
        }

        int totalUnused = 0;
        for (String s : text.split("\n")) {
            String tmp = s.trim();
            if (tmp.isEmpty() || tmp.startsWith("//")) {
                totalUnused++;
            }
        }

        return Math.max(0, rawLocs - totalUnused - totalCountWithComment);
    }
}
