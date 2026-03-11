package net.annedawson.bmi

/*

Last updated: Wednesday 11th March 2026, 10:31 PT
Date started: Friday 9th August 2024, 13:08 PT
Programmer: Anne Dawson
App: BMI
Purpose: Calculates Body Mass Index (BMI)
File: MainActivity.kt
Status: Remembers selected measurement units between app usages
*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.annedawson.bmi.ui.theme.BmiTheme
import net.annedawson.bmi.ui.theme.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.clickable

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.map

import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")
val IMPERIAL_UNITS_KEY = booleanPreferencesKey("imperial_units")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BmiTheme {

                BmiApp()

            }
        }
    }
}


@Composable
fun BmiApp() {
    var weightInput by rememberSaveable { mutableStateOf("") }
    // The String weight is now a state.  is observable mutable state.
    // Initially, the value of weight is an empty string
    // weight state was hoisted from EditNumberField
    //  and then passed to EditNumberField
    // remember and mutableStateOf are functions (that you can step into using the debugger)
    var heightInput by rememberSaveable { mutableStateOf("") }

    val weight = weightInput.toDoubleOrNull() ?: 0.0  //  convert String to Integer
    // toIntOrNull() parses the string to an Int number
    // and returns the result or null if the string is not a valid representation of a number.
    // ?: is the Elvis operator
    // The Elvis operator permits the assignment of what's on the left,
    // but if it's null, assign what's on the right

    val height = heightInput.toDoubleOrNull() ?: 0.0


    val focusManager = LocalFocusManager.current  // Unit 6: Set keyboard actions

    var moreDetails by rememberSaveable { mutableStateOf(false) }
    // moreDetails is now a state. it will be changed by the switch.

    // var imperialUnits by rememberSaveable { mutableStateOf(false) }
    // imperialUnits is now a state. it will be changed by the switch.

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val imperialUnits by context.dataStore.data
        .map { prefs -> prefs[IMPERIAL_UNITS_KEY] ?: false }
        .collectAsState(initial = false)

    val bmi = calculateBmi(weight, height, imperialUnits)

    val bmiValue = bmi.toFloatOrNull() ?: 0.0f

    // https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html
    val bmiCategory = when {
        bmiValue in 13.1..15.9 -> "Severe underweight"
        bmiValue in 16.0..18.4 -> "Underweight"
        bmiValue in 18.5..24.9 -> "Healthy weight"
        bmiValue in 25.0..29.9 -> "Overweight"
        bmiValue in 30.0..34.9 -> "Obesity Class 1"
        bmiValue in 35.0..39.9 -> "Obesity Class 2"
        bmiValue >= 40.0 -> "Obesity Class 3"
        else -> "Uncategorised"
       // else -> ""
    }

    // To hide the keyboard
    //val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            BmiTopAppBar(modifier = Modifier.systemBarsPadding())
        }
    )
    { innerPadding ->                    // ← capture the padding
        Column(
            modifier = Modifier
                // added the following line to allow scrolling
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)     // ← apply it first
                .padding(horizontal = 48.dp)  // ← then your own side padding
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            /*
            EditNumberField(
                label = R.string.weight,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(  // Unit 6: Set keyboard actions
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    // import androidx.compose.ui.focus.FocusDirection
                ),
                value = weightInput,  // value is what is displayed in the Textbox,
                // it is refreshed after the event below
                onValueChange = { weightInput = it }
            )
            // Note that the "it" parameter holds the updated value in the text box
            // and is used to update the state (weightInput), which triggers recomposition i.e.
            // re-calling the composables that use that state.

            // If the user types a single character into the text box,
            // the value in the text box changes.
            // That updated value is the "it" parameter.
            // Any input to the text box triggers the event to place the current value (it)
            // into the weightInput state, then as the state is observable,
            // the composable is run again with the new data,
            // so that the UI is redrawn with the new data.


             */

            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = {
                    if (!imperialUnits) {
                        Text(stringResource(R.string.weight_kg))
                    } else {
                        Text(stringResource(R.string.weight_lb))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(), // Add this for full width
                singleLine = true, // Add this for single line input
                shape = Shapes.large // Add this for rounded corners
            )

            OutlinedTextField(
                value = heightInput,
                onValueChange = { heightInput = it },
                label = {
                    if (!imperialUnits) {
                        Text(stringResource(R.string.height_cm))
                    } else {
                        Text(stringResource(R.string.height_in))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(), // Add this for full width
                singleLine = true, // Add this for single line input
                shape = Shapes.large // Add this for rounded corners
            )

            /*           EditNumberField(
                           label = R.string.height,
                           keyboardOptions = KeyboardOptions(
                               keyboardType = KeyboardType.Number,
                               imeAction = ImeAction.Done
                           ),
                           keyboardActions = KeyboardActions(  // Unit 6: Set keyboard actions
                               onDone = { focusManager.clearFocus() }),
                           value = heightInput,
                           onValueChange = { heightInput = it }
                       )
            */


            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.bmi, bmi),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = bmiCategory,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            ImperialDetailsRow(
                imperialUnits = imperialUnits,
                onImperialUnitsChanged = { newValue ->
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[IMPERIAL_UNITS_KEY] = newValue
                        }
                    }
                },
                moreDetails = moreDetails,
                onMoreDetailsChanged = { moreDetails = it }
            )

            if (moreDetails) {

                BmiCategories()

            }

        }
    }
}


@Composable
fun EditNumberField(
    @StringRes label: Int,  // annotation  denote to that the label parameter is expected to be a string resource reference
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,  // to be used in the TextField
    onValueChange: (String) -> Unit,  // to be used in the TextField
    //modifier: Modifier = Modifier
) {
    TextField(
        value = value,  // the amountInput state is passed to TextField value
        // a TextField's value is the string displayed in its text box on the UI
        onValueChange = onValueChange,  // onValueChange is a callback lambda event -
        // i.e. what must occur when the value changes.
        // "it" is the updated text the text box.
        // If you type in 123, "it" is first "1", then "12", then "123".
        // Set a break point (for line and lambda) on the line: var amountInput by ... above.
        // Run through the debugger and monitor the values of variables at the point of app suspension.
        // Press Resume program button to resume.
        // You can see the value of "it" change as the program runs.
        label = { Text(stringResource(label)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true
    )
}

@Composable
fun ImperialDetailsRow(
    imperialUnits: Boolean,
    onImperialUnitsChanged: (Boolean) -> Unit,
    moreDetails: Boolean,
    onMoreDetailsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        //.size(48.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.imperial_units),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(  // import androidx.compose.material3.Switch
            checked = imperialUnits,
            onCheckedChange = onImperialUnitsChanged,
            /*colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray
             )*/
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = if (isSystemInDarkTheme()) {
                    Color.LightGray
                } else {
                    Color.DarkGray // Or any dark color with good contrast
                }
            )
        )
        Spacer(modifier = Modifier.weight(3f))
        Text(
            text = stringResource(R.string.more_details),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(  // import androidx.compose.material3.Switch
            checked = moreDetails,
            onCheckedChange = onMoreDetailsChanged,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = if (isSystemInDarkTheme()) {
                    Color.LightGray
                } else {
                    Color.DarkGray // Or any dark color with good contrast
                }
            )
        )

    }
}


// change "private" to "internal" to allow
// module access for local testing, see:
// https://developer.android.com/codelabs/basic-android-kotlin-compose-write-automated-tests?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-2-pathway-3%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-write-automated-tests#3

@VisibleForTesting
// This makes the method public, but indicates to others that
// it's only public for testing purposes.
// In the Tip Time app, this function is used by the test TipCalculatorTests,
// calculate_20_percent_tip_no_roundup() test
internal fun calculateBmi(weight: Double, height: Double, imperialUnits: Boolean): String {
    var bmi: Double
    bmi = 0.0

    if (height > 0.0 && weight > 0.0) {
        if (!imperialUnits) {
            bmi = weight / (height * height) * 10000
        } else {
            bmi = weight / (height * height) * 703
        }
    }
    if (bmi > 200.0) {
        return " >200"
    } else {
        return String.format("%.1f", bmi)
        // convert the number to a 1 decimal place float formatted string
    }
}

@Composable
fun BmiTopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(50)),
            // to make image a circle with crop
            contentScale = ContentScale.Crop,
            painter = painterResource(R.drawable.bmi),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.top_app_bar_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(16.dp)

        )
    }
}

@Composable
fun BmiCategories() {
    val uriHandler = LocalUriHandler.current
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "BMI Categories", style = MaterialTheme.typography.titleMedium)
        if (isSystemInDarkTheme()) {
            BmiCategoryItem(category = "Underweight", range = "< 18.5", color = Color.White)
            BmiCategoryItem(category = "Healthy weight", range = "18.5 - 24.9", color = Color.White)
            BmiCategoryItem(category = "Overweight", range = "25 - 29.9", color = Color.White)
            BmiCategoryItem(category = "Obese", range = "≥ 30", color = Color.White)
        } else {
            BmiCategoryItem(category = "Underweight", range = "< 18.5", color = Color.Black)
            BmiCategoryItem(category = "Healthy weight", range = "18.5 - 24.9", color = Color.Black)
            BmiCategoryItem(category = "Overweight", range = "25 - 29.9", color = Color.Black)
            BmiCategoryItem(category = "Obese", range = "≥ 30", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Learn more about BMI",
            modifier = Modifier.clickable {
                uriHandler.openUri("https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html")
            },
            color = Color.Magenta,
            style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline)
        )
    }

}

@Composable
fun BmiCategoryItem(category: String, range: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        // colors = CardDefaults.cardColors(containerColor = color),elevation=
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = category, style = MaterialTheme.typography.titleMedium, color = color)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = range, style = MaterialTheme.typography.bodyMedium, color = color)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BMIScreenPreview() {
    BmiTheme {
        BmiApp()
    }
}


//@Preview(showBackground = true, backgroundColor = 0xFF00FF00)  // green
@Preview(showBackground = true)
@Composable
fun BMIScreenDarkThemePreview() {
    BmiTheme(darkTheme = true) {
        BmiApp()
    }
}