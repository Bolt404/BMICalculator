package com.example.bmicalculator;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;

public class bmiCalController {
    @FXML
    public TextField heightInput;
    @FXML
    public TextField weightInput;
    @FXML
    public Button calculateBmi;
    @FXML
    public ToggleGroup gender;
    @FXML
    public TextField ageInput;
    @FXML
    public TextField bmiField;
    @FXML
    public TextField bmiPrimeField;
    @FXML
    public TextField classificationField;

    /**
     * 3D Array Doc. [][][]
     * Indexes                         = [Layer = Gender] [Row = BMI] [Coloumn = Age]
     * Index [Layer = Gender]        = 0 = Female, 1 = Male, 2 = Default
     * Index [Row = BMI]             = BMI Value, 0 = Lowest, 1 = Middle, 2 = Highest
     * Index [Coloumn = Age]         = Age - 3 = Element Number
     * The Array covers, female and male, between the age <3 - >18.
     * And Default male & female >=19
     * The values in the array are from https://iform.dk/vaegttab/hvad-betyder-dit-bmi-tal
     * I am by no means an expert, these values are for educational purposes only and may not be accurate.
     */
    private static final double[][][] BMIDATA = {
            //Female
            {
                    // Columns: Lowest, Middle, Highest
                    {14.6, 17.6, 19.4},   // Age 3
                    {14.3, 17.4, 19.2},   // Age 4
                    {14.0, 17.2, 19.2},   // Age 5
                    {13.9, 17.3, 19.6},   // Age 6
                    {13.8, 17.7, 20.4},   // Age 7
                    {14.0, 18.3, 21.4},   // Age 8
                    {14.3, 19.0, 22.7},   // Age 9
                    {14.6, 19.8, 24.0},   // Age 10
                    {15.0, 20.7, 25.3},   // Age 11
                    {15.6, 21.6, 26.5},   // Age 12
                    {16.2, 22.5, 27.6},   // Age 13
                    {16.9, 23.3, 28.4},   // Age 14
                    {17.4, 23.9, 29.0},   // Age 15
                    {17.9, 24.3, 29.4},   // Age 16
                    {18.2, 24.7, 29.7},   // Age 17
                    {18.5, 25.0, 30.0}    // Age 18
            },
            //Male
            {
                    {14.8, 17.9, 19.5},   // Age 3
                    {14.5, 17.5, 19.2},   // Age 4
                    {14.3, 17.4, 19.3},   // Age 5
                    {14.1, 17.5, 19.8},   // Age 6
                    {14.0, 17.9, 20.6},   // Age 7
                    {14.1, 18.4, 21.6},   // Age 8
                    {14.4, 19.1, 22.7},   // Age 9
                    {14.6, 19.8, 24.0},   // Age 10
                    {15.0, 20.5, 25.1},   // Age 11
                    {15.4, 21.2, 26.0},   // Age 12
                    {15.8, 22.5, 27.6},   // Age 13
                    {16.4, 23.3, 28.4},   // Age 14
                    {17.0, 23.9, 29.0},   // Age 15
                    {17.5, 24.3, 29.4},   // Age 16
                    {18.2, 24.7, 29.7},   // Age 17
                    {18.5, 25.0, 30.0}    // Age 18
            },
            //Default
            {
                    {19,25,30} //Age 19 and up.
            }
    };

    /**
     * Main method of the program, onCalculateBmiClick runs all the logic, and is bound to the button with fx:id="calculateBmi"
     */
    @FXML
    protected void onCalculateBmiClick() {

        //Lets check if we are getting valid inputs from the user.
        //For ease of use we'll convert to booleans/flags
        boolean isHeightTrue = inputDoubleOrNot(heightInput);
        boolean isWeightTrue = inputDoubleOrNot(weightInput);
        boolean isAgeTrue = inputDoubleOrNot(ageInput);

        //We need the Age as a int for later.
        int ageAsInt = Integer.parseInt(ageInput.getText());


        //Lets see if there is any valid input. and give error messages if not.
        if (!isWeightTrue && !isHeightTrue && !isAgeTrue){
            showAlert("Please input weight in kg.\nPlease input height in cm.\nPlease input age in years.");
        } else if (!isHeightTrue){
            showAlert("Please input height in cm.");
        } else if (!isWeightTrue){
            showAlert("Please input weight in kg.");
        } else if (!isAgeTrue){
            showAlert("Please input age in years.");
        } else if (ageAsInt < 3){
            //As our program doesn't work with people younger than 3, we need to check for this.
            showAlert("Cannot calculated BMI on people younger than 3 years of age.");
            isAgeTrue = false; //Then set the flag to false, the program will run but the output will be nonsense, so lets just not bother.
        }


        //Setting up our radiobutton group, to see if people are male or female
        RadioButton selectedGenderButton = (RadioButton) gender.getSelectedToggle();
        String stringGender = selectedGenderButton.getText(); //I'm basing the logic of the textField in the group.

        //Let's use all our logic and show some results to the user if the flags are true.
        if (isWeightTrue && isHeightTrue && isAgeTrue){
            //Getting all the data we need.
            double bmi = calculateBmi(weightInput,heightInput); //Calculating out BMI
            double[] bmiIndex = returnBmiIndexDependOnAge(ageAsInt,stringGender); //Setting up our bmiIndex as an array.
            String bmiClass = bmiClassification(bmi, bmiIndex); // Finding what BMI class the users input is in.

            //Showing the BMI to the user.
            bmiField.setText(String.format("%.2f",bmi));

            //Showing the BMIClass to the user.
            classificationField.setText(String.format("%s", bmiClass));

            //Showing the BMI Prime to the user
            bmiPrimeField.setText(String.format("%.2f",bmiPrime(bmi,ageAsInt)));
        } else {
            //Else if the flags are false, we'll empty the text fields for easier reuse.
            weightInput.setText("");
            heightInput.setText("");
            ageInput.setText("");
        }
    }
    /**
     * Simple calculation for bmiPrime.
     * @param bmi input BMI as double
     * @param age input AGE as double
     * @return double
     */
    private double bmiPrime (double bmi, double age){
        return bmi/age;
    }

    /**
     * Dynamic module for checking where on the bmi scale a number is.
     * @param bmiNumber double Calculated BMI number
     * @param bmiIndex double[] The index to search against.
     * @return String
     */
    public static String bmiClassification (double bmiNumber, double[] bmiIndex){
        String resultFatOrNot = null; //Setting up our String.

        //Our underweight value is always under 0, normal between 0-1, overweight 1-2 and obese over 2.
        if (bmiNumber < bmiIndex[0]) {
            resultFatOrNot = "Underweight";
        } else if (bmiNumber >= bmiIndex[0] && bmiNumber < bmiIndex[1]) {
            resultFatOrNot = "Normal";
        } else if (bmiNumber >= bmiIndex[1] && bmiNumber < bmiIndex[2]) {
            resultFatOrNot = "Overweight";
        } else if (bmiNumber >= bmiIndex[2]) {
            resultFatOrNot = "Obese";
        }
        //return our String.
        return resultFatOrNot;
    }

    /**
     * Simple calculation for BMI, will try and parseDouble from TextField.
     * @param weightInput TextField looking for double.
     * @param heightInput TextField looking for double.
     * @return double
     */
    private double calculateBmi (TextField weightInput,TextField heightInput){
        //Setting up variables for easier viewing.
        double weight = Double.parseDouble(weightInput.getText());
        double height = Double.parseDouble(heightInput.getText());
        //Simple BMI Calculation.
        return (weight / (Math.pow((height/100), 2)));
    }

    /**
     * Setting up our index to search against dependent on age and gender.
     * @param age int, whole number for age.
     * @param gender String, Male or Female
     * @return double[]
     */
    private static double[] returnBmiIndexDependOnAge(int age, String gender) {
        //Preparing our variables.
        int genderIndex = 2;
        double[] bmiSet = new double[3];
        int ageIndex = age;

        //If the person is younger than 19, we need to take a different approach to age and account for gender.
        if (age < 19) {
            ageIndex = age - 3; //Our index goes from 0-16, but our ages are 3-18
            if (Objects.equals(gender, "Female")) {
                genderIndex = 0;
            } else if (Objects.equals(gender, "Male")) {
                genderIndex = 1;
            }
        //If the person is older than or 19, then we will use our default index.
        } else if (age >= 19) {
            ageIndex = 0;
        }

        for (int i = 0; i < BMIDATA[genderIndex][ageIndex].length; i++) {
            bmiSet[i] = BMIDATA[genderIndex][ageIndex][i];
        }

        return bmiSet;
    }
    /**
     * Try-catch for userinput validation, checking to see if valid double has been input.
     * @param name name of Textfield to check for valid Double or not.
     * @return true or false
     */
    private boolean inputDoubleOrNot (TextField name) {
        //Try and see if you can turn name.getText into a double.
        try {
            Double.parseDouble(name.getText());
        } catch (NumberFormatException e) {
            return false; // Return False.
        }
        return true; //Otherwise True.
    }

    /**
     * Popup Alert to show users.
     * @param message Message to send to user.
     */
    private void showAlert(String message) {
        //New alert, type Warning.
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}