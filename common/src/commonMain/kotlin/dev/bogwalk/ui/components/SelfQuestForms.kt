package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import dev.bogwalk.models.*
import dev.bogwalk.ui.style.SelfQuestTheme
import dev.bogwalk.ui.style.midDp
import dev.bogwalk.ui.style.preferredWidth
import dev.bogwalk.ui.style.smallDp

const val ADD_HEADER = "Add a new"
const val EDIT_HEADER = "Edit current"
const val NAME_TAG = "name field"
const val CONTENT_TAG = "content field"
const val LINK_TAG = "link field"
const val RADIO_TAG = "radio button"

@Composable
fun DeckDataForm(
    deck: Deck?,
    onConfirmDeckData: (Deck) -> Unit
) {
    var name by remember { mutableStateOf(deck?.name ?: "") }

    val focusManager = LocalFocusManager.current

    SelfQuestDataForm(
        header = "${if (deck == null) ADD_HEADER else EDIT_HEADER} collection",
        isSaveEnabled = name.isNotEmpty() && name != deck?.name,
        onSaveRequest = { onConfirmDeckData(Deck(deck?.id ?: 1, name, deck?.size ?: 0)) }
    ) {
        SelfQuestTextField(
            input = name,
            label = "Name",
            modifier = Modifier.testTag(NAME_TAG).fillMaxWidth(),
            focusManager = focusManager,
            inputMaxChar = DataLength.DECK_NAME,
        ) {
            name = it.take(DataLength.DECK_NAME)
        }
    }
}

@Composable
fun QuestionDataForm(
    question: Question?,
    onConfirmQuestionData: (Question) -> Unit
) {
    var content by remember { mutableStateOf(question?.content ?: "") }
    var option1 by remember { mutableStateOf(question?.optionalAnswers?.get(0) ?: "") }
    var option2 by remember { mutableStateOf(question?.optionalAnswers?.get(1) ?: "") }
    var option3 by remember { mutableStateOf(question?.optionalAnswers?.get(2) ?: "") }
    var option4 by remember { mutableStateOf(question?.optionalAnswers?.get(3) ?: "") }
    var correct by remember { mutableStateOf(question?.expectedAnswer ?: "") }

    val focusManager = LocalFocusManager.current

    SelfQuestDataForm(
        header = "${if (question == null) ADD_HEADER else EDIT_HEADER} question",
        isSaveEnabled = content.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty()
                && option3.isNotEmpty() && option4.isNotEmpty() && correct.isNotEmpty()
                && setOf(option1, option2, option3, option4).size == 4,
        onSaveRequest = { onConfirmQuestionData(Question(
            question?.id ?: 1, content, listOf(option1, option2, option3, option4), correct, question?.review
        )) }
    ) {
        SelfQuestTextField(
            input = content,
            label = "Question",
            modifier = Modifier.testTag(CONTENT_TAG).fillMaxWidth(),
            isSingleLine = false,
            focusManager = focusManager,
            inputMaxChar = DataLength.QUESTION_CONTENT,
        ) {
            content = it.take(DataLength.QUESTION_CONTENT)
        }
        OptionRow(
            option = option1,
            optionLabel = "Answer 1",
            isInvalid = option1.isNotEmpty() && (option1 == option2 || option1 == option3 || option1 == option4),
            correctOption = correct,
            focusManager = focusManager,
            takeMaxChar = { option1 = it.take(DataLength.QUESTION_OPTION) },
            onSelectCorrect = { correct = option1 }
        )
        OptionRow(
            option = option2,
            optionLabel = "Answer 2",
            isInvalid = option2.isNotEmpty() && (option2 == option1 || option2 == option3 || option2 == option4),
            correctOption = correct,
            focusManager = focusManager,
            takeMaxChar = { option2 = it.take(DataLength.QUESTION_OPTION) },
            onSelectCorrect = { correct = option2 }
        )
        OptionRow(
            option = option3,
            optionLabel = "Answer 3",
            isInvalid = option3.isNotEmpty() && (option3 == option1 || option3 == option2 || option3 == option4),
            correctOption = correct,
            focusManager = focusManager,
            takeMaxChar = { option3 = it.take(DataLength.QUESTION_OPTION) },
            onSelectCorrect = { correct = option3 }
        )
        OptionRow(
            option = option4,
            optionLabel = "Answer 4",
            isInvalid = option4.isNotEmpty() && (option4 == option1 || option4 == option2 || option4 == option3),
            correctOption = correct,
            focusManager = focusManager,
            takeMaxChar = { option4 = it.take(DataLength.QUESTION_OPTION) },
            onSelectCorrect = { correct = option4 }
        )
    }
}

@Composable
fun ReviewDataForm(
    review: Review?,
    onConfirmReviewData: (Review) -> Unit
) {
    var content by remember { mutableStateOf(review?.content ?: "") }
    val refNames = remember { mutableStateListOf<String>().apply { review?.references?.map {it.first}?.let { addAll(it) } } }
    val refLinks = remember { mutableStateListOf<String>().apply { review?.references?.map {it.second}?.let { addAll(it) } } }

    val focusManager = LocalFocusManager.current

    SelfQuestDataForm(
        header = "$EDIT_HEADER review",
        isSaveEnabled = content.isNotEmpty() || refNames.zip(refLinks).any { it.first.isNotEmpty() && it.second.isNotEmpty() },
        onSaveRequest = {
            val newRefs = refNames.zip(refLinks).filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
            onConfirmReviewData(Review(content, newRefs))
        }
    ) {
        SelfQuestTextField(
            input = content,
            label = "Info",
            modifier = Modifier.testTag(CONTENT_TAG).fillMaxWidth(),
            isSingleLine = false,
            focusManager = focusManager,
            inputMaxChar = DataLength.REVIEW_CONTENT,
        ) {
            content = it.take(DataLength.REVIEW_CONTENT)
        }
        refNames.forEachIndexed { index, name ->
            key("link$index") {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelfQuestTextField(
                        input = name,
                        label = "Name",
                        modifier = Modifier.testTag(LINK_TAG).weight(.4f),
                        focusManager = focusManager,
                        inputMaxChar = DataLength.REVIEW_REF_NAME,
                        takeMaxChar = { refNames[index] = it.take(DataLength.REVIEW_REF_NAME) }
                    )
                    SelfQuestTextField(
                        input = refLinks[index],
                        label = "URL",
                        modifier = Modifier.testTag(LINK_TAG).weight(.6f),
                        focusManager = focusManager,
                        inputMaxChar = DataLength.REVIEW_REF_URI,
                        takeMaxChar = { refLinks[index] = it.take(DataLength.REVIEW_REF_URI) }
                    )
                }
            }
        }
        AddLinkButton {
            refNames.add("")
            refLinks.add("")
        }
    }
}

@Composable
private fun SelfQuestDataForm(
    header: String,
    isSaveEnabled: Boolean,
    onSaveRequest: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(smallDp)
    ) {
        SelfQuestHeader(header, Modifier.align(Alignment.Start))
        Spacer(Modifier.height(midDp))
        content()
        Spacer(Modifier.height(midDp))
        SaveFormButton(isSaveEnabled, onSaveRequest)
    }
}

@Composable
private fun OptionRow(
    option: String,
    optionLabel: String,
    isInvalid: Boolean,
    correctOption: String,
    focusManager: FocusManager,
    takeMaxChar: (String) -> Unit,
    onSelectCorrect: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelfQuestTextField(
            input = option,
            label = optionLabel,
            modifier = Modifier.testTag(optionLabel).weight(1f),
            error = isInvalid,
            focusManager = focusManager,
            inputMaxChar = DataLength.QUESTION_OPTION,
            takeMaxChar = takeMaxChar
        )
        RadioButton(
            selected = correctOption.isNotEmpty() && option == correctOption,
            onClick = { onSelectCorrect(option) },
            modifier = Modifier.testTag(RADIO_TAG),
            enabled = option.isNotEmpty(),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SelfQuestTextField(
    input: String,
    label: String,
    modifier: Modifier,
    isSingleLine: Boolean = true,
    error: Boolean = false,
    focusManager: FocusManager,
    inputMaxChar: Int,
    takeMaxChar: (String) -> Unit
) {
    OutlinedTextField(
        value = input,
        onValueChange = {
            takeMaxChar(it)
            if (it.length > inputMaxChar) focusManager.moveFocus(FocusDirection.Down)
        },
        modifier = modifier.padding(smallDp),
        label = { Text(label) },
        isError = error,
        singleLine = isSingleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview
@Composable
private fun DeckDataFormAddNewPreview() {
    SelfQuestTheme {
        DeckDataForm( null) {}
    }
}

@Preview
@Composable
private fun DeckDataFormEditShortPreview() {
    SelfQuestTheme {
        DeckDataForm(Deck(1, "Equine", 5)) {}
    }
}

@Preview
@Composable
private fun DeckDataFormEditLongPreview() {
    SelfQuestTheme {
        DeckDataForm(Deck(1, "A".repeat(DataLength.DECK_NAME), 5)) {}
    }
}

@Preview
@Composable
private fun QuestionDataFormAddNewPreview() {
    SelfQuestTheme {
        QuestionDataForm(null) {}
    }
}

@Preview
@Composable
private fun QuestionDataFormEditPreview() {
    SelfQuestTheme {
        QuestionDataForm(Question(
            1, "?".repeat(256),
            listOf("Apple", "Boat", "Car", "D".repeat(DataLength.QUESTION_OPTION)),
            "Car", null
        )) {}
    }
}

@Preview
@Composable
private fun QuestionDataFormInvalidDuplicatesPreview() {
    SelfQuestTheme {
        QuestionDataForm(Question(
            1, "Fake question",
            listOf("Apple", "Apple", "Car", "Dog"),
            "Apple", null
        )) {}
    }
}

@Preview
@Composable
private fun ReviewDataFormAddNewPreview() {
    SelfQuestTheme {
        ReviewDataForm(null) {}
    }
}

@Preview
@Composable
private fun ReviewDataFormEdit1Preview() {
    SelfQuestTheme {
        ReviewDataForm(Review(review, emptyList())) {}
    }
}

@Preview
@Composable
private fun ReviewDataFormEdit2Preview() {
    SelfQuestTheme {
        ReviewDataForm(Review("", references)) {}
    }
}