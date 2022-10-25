package dev.bogwalk.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import dev.bogwalk.models.DataLength
import dev.bogwalk.models.Question
import dev.bogwalk.ui.style.*

@Composable
fun QuestionDataForm(
    question: Question?,
    onConfirmQuestionData: (Question) -> Unit
) {
    var content by remember { mutableStateOf(question?.content ?: "") }
    val contentMaxChar = DataLength.QuestionContent
    var option1 by remember { mutableStateOf(question?.optionalAnswer1 ?: "") }
    var option2 by remember { mutableStateOf(question?.optionalAnswer2 ?: "") }
    var option3 by remember { mutableStateOf(question?.optionalAnswer3 ?: "") }
    var option4 by remember { mutableStateOf(question?.optionalAnswer4 ?: "") }
    val optionMaxChar = DataLength.QuestionOption
    var correct by remember { mutableStateOf(question?.expectedAnswer ?: "") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.widthIn(preferredWidth.first, preferredWidth.second).padding(cardPadding)
    ) {
        Text(
            text = "${if (question == null) ADD_HEADER else EDIT_HEADER} question",
            modifier = Modifier.align(Alignment.Start).padding(start = cardPadding),
            style = MaterialTheme.typography.h4
        )
        Spacer(Modifier.height(innerPadding))
        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it.take(contentMaxChar)
                if (it.length > contentMaxChar) focusManager.moveFocus(FocusDirection.Down)
            },
            modifier = Modifier.padding(cardPadding).fillMaxWidth(),
            label = { Text("Question") },
            colors = getTextFieldColors()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = option1,
                onValueChange = {
                    option1 = it.take(optionMaxChar)
                    if (it.length > optionMaxChar) focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier.padding(cardPadding).weight(1f),
                label = { Text("Answer 1") },
                colors = getTextFieldColors()
            )
            RadioButton(
                selected = correct.isNotEmpty() && option1 == correct,
                onClick = { correct = option1 },
                enabled = option1.isNotEmpty(),
                colors = getRadioButtonColors()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = option2,
                onValueChange = {
                    option2 = it.take(optionMaxChar)
                    if (it.length > optionMaxChar) focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier.padding(cardPadding).weight(1f),
                label = { Text("Answer 2") },
                colors = getTextFieldColors()
            )
            RadioButton(
                selected = correct.isNotEmpty() && option2 == correct,
                onClick = { correct = option2 },
                enabled = option2.isNotEmpty(),
                colors = getRadioButtonColors()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = option3,
                onValueChange = {
                    option3 = it.take(optionMaxChar)
                    if (it.length > optionMaxChar) focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier.padding(cardPadding).weight(1f),
                label = { Text("Answer 3") },
                colors = getTextFieldColors()
            )
            RadioButton(
                selected = correct.isNotEmpty() && option3 == correct,
                onClick = { correct = option3 },
                enabled = option3.isNotEmpty(),
                colors = getRadioButtonColors()
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = option4,
                onValueChange = {
                    option4 = it.take(optionMaxChar)
                    if (it.length > optionMaxChar) focusManager.moveFocus(FocusDirection.Down)
                },
                modifier = Modifier.padding(cardPadding).weight(1f),
                label = { Text("Answer 4") },
                colors = getTextFieldColors()
            )
            RadioButton(
                selected = correct.isNotEmpty() && option4 == correct,
                onClick = { correct = option4 },
                enabled = option4.isNotEmpty(),
                colors = getRadioButtonColors()
            )
        }
        Spacer(Modifier.height(innerPadding))
        Button(
            onClick = { onConfirmQuestionData(Question(
                question?.id ?: 1, content, option1, option2, option3, option4, option1
            )) },
            modifier = Modifier.padding(cardPadding),
            enabled = content.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty() && option3.isNotEmpty() && option4.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onSurface
            )
        ) {
            Text(text = SAVE_TEXT, style = MaterialTheme.typography.button)
        }
    }
}

@Composable
private fun getTextFieldColors(): TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = MaterialTheme.colors.primary,
    focusedLabelColor = MaterialTheme.colors.primary
)

@Composable
private fun getRadioButtonColors(): RadioButtonColors = RadioButtonDefaults.colors(
    selectedColor = MaterialTheme.colors.primary
)

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
            1, "This is the original question?", "Apple", "Boat", "Car", "Donkey", "Car"
        )) {}
    }
}