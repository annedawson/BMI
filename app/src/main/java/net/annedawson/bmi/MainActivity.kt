package net.annedawson.bmi

/*
Date: Friday 9th August 2024, 13:08 PT
Programmer: Anne Dawson
App: BMI
File: MainActivity.kt
Purpose: Introduction to state in Compose
From: https://developer.android.com/codelabs/basic-android-kotlin-compose-using-state?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-2-pathway-3%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-using-state#0
Status: Completed to end of Unit 2, Pathway 3, Part 5
        Part 5 Write an instrumentation (UI) test
https://developer.android.com/codelabs/basic-android-kotlin-compose-write-automated-tests?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-2-pathway-3%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-write-automated-tests#4
*/
import android.annotation.SuppressLint
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


// the above from Woof app to make images round


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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    var metricUnits by rememberSaveable { mutableStateOf(false) }
    // moreDetails is now a state. it will be changed by the switch.

    val bmi = calculateBmi(weight, height, metricUnits)

    // bmi is now a state  ??

    var bmiCategory by rememberSaveable {
        mutableStateOf("")
    }
    val bmiValue = bmi.toFloatOrNull() ?: 0.0f

    // https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html
    bmiCategory = when {
        bmiValue in 13.1..15.9 -> "Severe underweight"
        bmiValue in 16.0..18.4 -> "Underweight"
        bmiValue in 18.5..24.9 -> "Healthy weight"
        bmiValue in 25.0..29.9 -> "Overweight"
        bmiValue in 30.0..34.9 -> "Obesity Class 1"
        bmiValue in 35.0..39.9 -> "Obesity Class 2"
        bmiValue >= 40.0 -> "Obesity Class 3"
//        else -> "Uncategorized"
        else -> ""
    }


    // To hide the keyboard
    val keyboardController = LocalSoftwareKeyboardController.current


    Scaffold(
        topBar = {
            BmiTopAppBar(modifier = Modifier.systemBarsPadding())
        }
    )
    {
        Column(
            modifier = Modifier
                // added the following line to allow scrolling
                .verticalScroll(rememberScrollState())
                // try changing the padding to 40 from 48
                .padding(48.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                //text = stringResource(id = R.string.calculate_bmi),
                // try replacing the line above with the line below.
                // In horizontal orientation the text "Calculate BMI"
                // was visible on scrolling, hence I replaced with empty text.
                text = "",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
                    if (metricUnits) {
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
                    if (metricUnits) {
                        Text(stringResource(R.string.height_cm))
                    } else {
                        Text(stringResource(R.string.height_in))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
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
            MetricDetailsRow(metricUnits = metricUnits, onMetricUnitsChanged = { metricUnits = it },moreDetails = moreDetails, onMoreDetailsChanged = { moreDetails = it })
            if (moreDetails) {
                // To hide the keyboard
                //keyboardController?.hide()
                BmiCategories()
                /*Image(
                    painter = painterResource(id = R.drawable.bmi),
                    contentDescription = "My Image"
                )*/
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
fun MetricDetailsRow(
    metricUnits: Boolean,
    onMetricUnitsChanged: (Boolean) -> Unit,
    moreDetails: Boolean,
    onMoreDetailsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        //.size(48.dp),
        horizontalArrangement =   Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.metric_units),
            fontSize = 12.sp)
        Spacer(modifier = Modifier.weight(1f))
        Switch(  // import androidx.compose.material3.Switch
            checked = metricUnits,
            onCheckedChange = onMetricUnitsChanged,
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
        Text(text = stringResource(R.string.more_details),
            fontSize = 12.sp)
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
internal fun calculateBmi(weight: Double, height: Double, metricUnits: Boolean): String {
    var bmi: Double
    bmi = 0.0

    if (height > 0.0 && weight > 0.0) {
        if (metricUnits) {
            bmi = weight / (height * height) * 10000
        }
        else {
            bmi = weight / (height * height) * 703
        }
    }
    return String.format("%.1f", bmi)
    // convert the number to a 1 decimal place float formatted string
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
        }
        else {
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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