package tomoki1207.varjump

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.navigation.NavigationUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil


class GotoInferredTypeDeclarationAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val offset = e.getRequiredData(CommonDataKeys.CARET).offset
        val psiElement = e.getRequiredData(CommonDataKeys.PSI_FILE).findElementAt(offset)
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)

        if (psiElement is PsiKeyword && PsiKeyword.VAR == psiElement.getText()) {
            val parent: PsiElement = psiElement.getParent()
            if (parent is PsiTypeElement && parent.isInferredType) {
                val symbolTypes = collectClasses(parent.type).toTypedArray()
                if (symbolTypes.isEmpty()) {
                    HintManager.getInstance().showInformationHint(editor, "Cannot find declaration to go to")
                } else {
                    NavigationUtil.getPsiElementPopup(symbolTypes, "Jump to ...")
                        .showInBestPositionFor(editor)
                }
            }
        }
    }

    private fun collectClasses(type: PsiType): List<PsiClass> {
        val classType = PsiUtil.resolveGenericsClassInType(type)
        return if (classType == PsiClassType.ClassResolveResult.EMPTY) {
            emptyList()
        } else {
            listOf(classType.element!!) +
                    classType.substitutor.substitutionMap.values.flatMap {
                        collectClasses(it)
                    }
        }
    }
}