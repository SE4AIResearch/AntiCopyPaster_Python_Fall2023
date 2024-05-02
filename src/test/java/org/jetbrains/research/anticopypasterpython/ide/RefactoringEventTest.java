package org.jetbrains.research.anticopypasterpython.ide;

import com.github.weisj.jsvg.R;
import com.intellij.lang.FileASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.resolve.RatedResolveResult;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RefactoringEventTest {

    private RefactoringEvent refactoringEvent;
    private PyFile testFile;

    @BeforeEach
    public void beforeTest() {
        refactoringEvent = new RefactoringEvent(null, null, null, 0, null, null, 0);
        testFile = new PyFile() {
            @Override
            public List<PyStatement> getStatements() {
                return null;
            }

            @Override
            public @NotNull List<PyClass> getTopLevelClasses() {
                return null;
            }

            @Override
            public @NotNull List<PyFunction> getTopLevelFunctions() {
                return null;
            }

            @Override
            public List<PyTargetExpression> getTopLevelAttributes() {
                return null;
            }

            @Override
            public @Nullable PyFunction findTopLevelFunction(@NotNull String s) {
                return null;
            }

            @Override
            public @Nullable PyClass findTopLevelClass(@NonNls @NotNull String s) {
                return null;
            }

            @Override
            public @Nullable PyTargetExpression findTopLevelAttribute(@NotNull String s) {
                return null;
            }

            @Override
            public @NotNull List<PyTypeAliasStatement> getTypeAliasStatements() {
                return null;
            }

            @Override
            public @Nullable PyTypeAliasStatement findTypeAliasStatement(@NotNull String s) {
                return null;
            }

            @Override
            public LanguageLevel getLanguageLevel() {
                return null;
            }

            @Override
            public @NotNull List<PyFromImportStatement> getFromImports() {
                return null;
            }

            @Override
            public @Nullable PsiElement findExportedName(String s) {
                return null;
            }

            @Override
            public @NotNull Iterable<PyElement> iterateNames() {
                return null;
            }

            @Override
            public @NotNull List<RatedResolveResult> multiResolveName(@NotNull String s) {
                return null;
            }

            @Override
            public @NotNull List<RatedResolveResult> multiResolveName(@NotNull String s, boolean b) {
                return null;
            }

            @Override
            public @Nullable PsiElement getElementNamed(String s) {
                return null;
            }

            @Override
            public @NotNull List<PyImportElement> getImportTargets() {
                return null;
            }

            @Override
            public @Nullable List<String> getDunderAll() {
                return null;
            }

            @Override
            public boolean hasImportFromFuture(FutureFeature futureFeature) {
                return false;
            }

            @Override
            public String getDeprecationMessage() {
                return null;
            }

            @Override
            public List<PyImportStatementBase> getImportBlock() {
                return null;
            }

            @Override
            public VirtualFile getVirtualFile() {
                return null;
            }

            @Override
            public PsiDirectory getContainingDirectory() {
                return null;
            }

            @Override
            public PsiDirectory getParent() {
                return null;
            }

            @Override
            public long getModificationStamp() {
                return 0;
            }

            @Override
            public @NotNull PsiFile getOriginalFile() {
                return null;
            }

            @Override
            public @NotNull FileType getFileType() {
                return null;
            }

            @Override
            public PsiFile @NotNull [] getPsiRoots() {
                return new PsiFile[0];
            }

            @Override
            public @NotNull FileViewProvider getViewProvider() {
                return null;
            }

            @Override
            public FileASTNode getNode() {
                return null;
            }

            @Override
            public void subtreeChanged() {

            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean processChildren(@NotNull PsiElementProcessor<? super PsiFileSystemItem> psiElementProcessor) {
                return false;
            }

            @Override
            public @Nullable String getDocStringValue() {
                return null;
            }

            @Override
            public @Nullable StructuredDocString getStructuredDocString() {
                return null;
            }

            @Override
            public @Nullable PyStringLiteralExpression getDocStringExpression() {
                return null;
            }

            @Override
            public @Nullable @NlsSafe String getName() {
                return null;
            }

            @Override
            public @Nullable ItemPresentation getPresentation() {
                return null;
            }

            @Override
            public void checkSetName(String s) throws IncorrectOperationException {

            }

            @Override
            public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
                return null;
            }

            @Override
            public @NotNull Project getProject() throws PsiInvalidElementAccessException {
                return null;
            }

            @Override
            public @NotNull Language getLanguage() {
                return null;
            }

            @Override
            public PsiManager getManager() {
                return null;
            }

            @Override
            public @NotNull PsiElement @NotNull [] getChildren() {
                return new PsiElement[0];
            }

            @Override
            public PsiElement getFirstChild() {
                return null;
            }

            @Override
            public PsiElement getLastChild() {
                return null;
            }

            @Override
            public PsiElement getNextSibling() {
                return null;
            }

            @Override
            public PsiElement getPrevSibling() {
                return null;
            }

            @Override
            public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
                return null;
            }

            @Override
            public TextRange getTextRange() {
                return null;
            }

            @Override
            public int getStartOffsetInParent() {
                return 0;
            }

            @Override
            public int getTextLength() {
                return 0;
            }

            @Override
            public @Nullable PsiElement findElementAt(int i) {
                return null;
            }

            @Override
            public @Nullable PsiReference findReferenceAt(int i) {
                return null;
            }

            @Override
            public int getTextOffset() {
                return 0;
            }

            @Override
            public @NlsSafe String getText() {
                return null;
            }

            @Override
            public char @NotNull [] textToCharArray() {
                return new char[0];
            }

            @Override
            public PsiElement getNavigationElement() {
                return null;
            }

            @Override
            public PsiElement getOriginalElement() {
                return null;
            }

            @Override
            public boolean textMatches(@NotNull @NonNls CharSequence charSequence) {
                return false;
            }

            @Override
            public boolean textMatches(@NotNull PsiElement psiElement) {
                return false;
            }

            @Override
            public boolean textContains(char c) {
                return false;
            }

            @Override
            public void accept(@NotNull PsiElementVisitor psiElementVisitor) {

            }

            @Override
            public void acceptChildren(@NotNull PsiElementVisitor psiElementVisitor) {

            }

            @Override
            public PsiElement copy() {
                return null;
            }

            @Override
            public PsiElement add(@NotNull PsiElement psiElement) throws IncorrectOperationException {
                return null;
            }

            @Override
            public PsiElement addBefore(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
                return null;
            }

            @Override
            public PsiElement addAfter(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
                return null;
            }

            @Override
            public void checkAdd(@NotNull PsiElement psiElement) throws IncorrectOperationException {

            }

            @Override
            public PsiElement addRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
                return null;
            }

            @Override
            public PsiElement addRangeBefore(@NotNull PsiElement psiElement, @NotNull PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
                return null;
            }

            @Override
            public PsiElement addRangeAfter(PsiElement psiElement, PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
                return null;
            }

            @Override
            public void delete() throws IncorrectOperationException {

            }

            @Override
            public void checkDelete() throws IncorrectOperationException {

            }

            @Override
            public void deleteChildRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {

            }

            @Override
            public PsiElement replace(@NotNull PsiElement psiElement) throws IncorrectOperationException {
                return null;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public boolean isWritable() {
                return false;
            }

            @Override
            public @Nullable PsiReference getReference() {
                return null;
            }

            @Override
            public PsiReference @NotNull [] getReferences() {
                return new PsiReference[0];
            }

            @Override
            public <T> @Nullable T getCopyableUserData(@NotNull Key<T> key) {
                return null;
            }

            @Override
            public <T> void putCopyableUserData(@NotNull Key<T> key, @Nullable T t) {

            }

            @Override
            public boolean processDeclarations(@NotNull PsiScopeProcessor psiScopeProcessor, @NotNull ResolveState resolveState, @Nullable PsiElement psiElement, @NotNull PsiElement psiElement1) {
                return false;
            }

            @Override
            public @Nullable PsiElement getContext() {
                return null;
            }

            @Override
            public boolean isPhysical() {
                return false;
            }

            @Override
            public @NotNull GlobalSearchScope getResolveScope() {
                return null;
            }

            @Override
            public @NotNull SearchScope getUseScope() {
                return null;
            }

            @Override
            public boolean isEquivalentTo(PsiElement psiElement) {
                return false;
            }

            @Override
            public Icon getIcon(int i) {
                return null;
            }

            @Override
            public <T> @Nullable T getUserData(@NotNull Key<T> key) {
                return null;
            }

            @Override
            public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

            }
        };
    }

    @Test
    public void testPrintMessage() {
        System.out.println("Message = Test 1");
        assertTrue(true);
    }

    @Test
    public void testPrintMessage2() {
        System.out.println("Message2 = Test 2");
        assertTrue(true);
    }

    @Test
    public void testNullRefactorEvent() {
        //Baseline "null" test to see that our testing folder can access src files
        assertNull(refactoringEvent.getFile());
    }

    @Test
    public void testNotNullRefactorEvent() {
        RefactoringEvent testEvent = new RefactoringEvent(testFile, null, null, 0, null, null, 0);
        assertNotEquals(null, testEvent.getFile());
    }

    @Test
    public void testNonZeroMatches() {
        RefactoringEvent testEvent = new RefactoringEvent(null, null, null, 3, null, null, 3);
        assertEquals(3, testEvent.getMatches());
    }
}
