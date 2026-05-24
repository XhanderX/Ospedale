/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.model;

/**
 *
 * @author USER
 */
public class Prescription {

    private final String medicationName;
    private final double dose;
    private final String administrationRoute;
    private final int treatmentDuration;
    private final String additionalInstructions;
    private final int frequency;

    public Prescription(String medicationName, double dose, String administrationRoute, int treatmentDuration, String additionalInstructions, int frequency) {
        this.medicationName = medicationName;
        this.dose = dose;
        this.administrationRoute = administrationRoute;
        this.treatmentDuration = treatmentDuration;
        this.additionalInstructions = additionalInstructions;
        this.frequency = frequency;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public double getDose() {
        return dose;
    }

    public String getAdministrationRoute() {
        return administrationRoute;
    }

    public int getTreatmentDuration() {
        return treatmentDuration;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public int getFrequency() {
        return frequency;
    }

}
