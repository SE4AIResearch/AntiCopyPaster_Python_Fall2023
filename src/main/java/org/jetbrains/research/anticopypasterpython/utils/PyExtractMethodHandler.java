// NOTICE: This class is derived from
// https://github.com/JetBrains/intellij-community/blob/991b799a75c3a88e41ca22d68f1dcd8e280df8b2/python/python-psi-impl/src/com/jetbrains/python/refactoring/extractmethod/PyExtractMethodHandler.java
// It was modified to instead serve as a boolean checker method for extraction, as no such method otherwise exists.

// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.research.anticopypasterpython.utils;

import com.intellij.codeInsight.codeFragment.CannotCreateCodeFragmentException;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.jetbrains.python.PyPsiBundle;
import com.jetbrains.python.codeInsight.codeFragment.PyCodeFragment;
import com.jetbrains.python.codeInsight.codeFragment.PyCodeFragmentUtil;
import com.jetbrains.python.codeInsight.controlflow.ScopeOwner;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.impl.PyPsiUtils;
import com.jetbrains.python.refactoring.PyRefactoringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyExtractMethodHandler {

    // NOTICE: This method is heavily derived from the invokeOnEditor interface method override from
    // https://github.com/JetBrains/intellij-community/blob/991b799a75c3a88e41ca22d68f1dcd8e280df8b2/python/python-psi-impl/src/com/jetbrains/python/refactoring/extractmethod/PyExtractMethodHandler.java
    // It was modified to instead serve as a boolean checker method for extraction, as no such method otherwise exists.
    public static boolean checkExtraction(final Project project, final Editor editor, final PsiFile file) {
        CommonRefactoringUtil.checkReadOnlyStatus(project, file);
        PsiElement element1 = null;
        PsiElement element2 = null;
        final SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            element1 = file.findElementAt(selectionModel.getSelectionStart());
            element2 = file.findElementAt(selectionModel.getSelectionEnd() - 1);
        }
        else {
            final CaretModel caretModel = editor.getCaretModel();
            final Document document = editor.getDocument();
            int lineNumber = document.getLineNumber(caretModel.getOffset());
            if ((lineNumber >= 0) && (lineNumber < document.getLineCount())) {
                element1 = file.findElementAt(document.getLineStartOffset(lineNumber));
                element2 = file.findElementAt(document.getLineEndOffset(lineNumber) - 1);
            }
        }
        // Pass comments and whitespaces
        element1 = PyPsiUtils.getNextSignificantLeaf(element1, false);
        element2 = PyPsiUtils.getPrevSignificantLeaf(element2, false);
        if (element1 == null || element2 == null) {
            CommonRefactoringUtil.showErrorHint(project, editor,
                    PyPsiBundle.message("refactoring.extract.method.error.bad.selection"),
                    RefactoringBundle.message("extract.method.title"), "refactoring.extractMethod");
            return false;
        }
        if (rangeBelongsToSameClassBody(element1, element2)) {
            CommonRefactoringUtil.showErrorHint(project, editor, PyPsiBundle.message("refactoring.extract.method.error.class.level"),
                    RefactoringBundle.message("extract.method.title"), "refactoring.extractMethod");
            return false;
        }

        final Couple<PsiElement> statements = getStatementsRange(element1, element2);
        if (statements != null) {
            final ScopeOwner owner = PsiTreeUtil.getParentOfType(statements.getFirst(), ScopeOwner.class);
            if (owner == null) {
                return false;
            }
            final PyCodeFragment fragment;
            try {
                fragment = PyCodeFragmentUtil.createCodeFragment(owner, element1, element2);
            }
            catch (CannotCreateCodeFragmentException e) {
                CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(),
                        RefactoringBundle.message("extract.method.title"), "refactoring.extractMethod");
                return false;
            }
            // PyExtractMethodUtil.extractFromStatements(project, editor, fragment, statements.getFirst(), statements.getSecond());
            return true;
        }

        final PsiElement expression = PyRefactoringUtil.getSelectedExpression(project, file, element1, element2);
        if (expression != null) {
            final ScopeOwner owner = PsiTreeUtil.getParentOfType(element1, ScopeOwner.class);
            if (owner == null) {
                return false;
            }
            final PyCodeFragment fragment;
            try {
                fragment = PyCodeFragmentUtil.createCodeFragment(owner, element1, element2);
            }
            catch (CannotCreateCodeFragmentException e) {
                CommonRefactoringUtil.showErrorHint(project, editor, e.getMessage(),
                        RefactoringBundle.message("extract.method.title"), "refactoring.extractMethod");
                return false;
            }
            // PyExtractMethodUtil.extractFromExpression(project, editor, fragment, expression);
            return true;
        }

        CommonRefactoringUtil.showErrorHint(project, editor,
                PyPsiBundle.message("refactoring.extract.method.error.bad.selection"),
                RefactoringBundle.message("extract.method.title"), "refactoring.extractMethod");
        return false;
    }

    private static boolean rangeBelongsToSameClassBody(@NotNull PsiElement element1, @NotNull PsiElement element2) {
        final PyClass firstScopeOwner = PsiTreeUtil.getParentOfType(element1, PyClass.class, false, ScopeOwner.class);
        final PyClass secondScopeOwner = PsiTreeUtil.getParentOfType(element2, PyClass.class, false, ScopeOwner.class);
        return firstScopeOwner != null && firstScopeOwner == secondScopeOwner;
    }

    @Nullable
    private static Couple<PsiElement> getStatementsRange(final PsiElement element1, final PsiElement element2) {
        final PsiElement parent = PsiTreeUtil.findCommonParent(element1, element2);
        if (parent == null) {
            return null;
        }

        final PyElement statementList = PyPsiUtils.getStatementList(parent);
        if (statementList == null) {
            return null;
        }

        final PsiElement statement1 = PyPsiUtils.getParentRightBefore(element1, statementList);
        final PsiElement statement2 = PyPsiUtils.getParentRightBefore(element2, statementList);
        if (statement1 == null || statement2 == null){
            return null;
        }

        // return elements if they are really first and last elements of statements
        if (element1 == PsiTreeUtil.getDeepestFirst(statement1) &&
                element2 == PyPsiUtils.getPrevSignificantLeaf(PsiTreeUtil.getDeepestLast(statement2), !(element2 instanceof PsiComment))) {
            return Couple.of(statement1, statement2);
        }
        return null;
    }
}
