package com.dit.airbnb.util;

public class MaxMinApartmentValues {

    public MaxMinApartmentValues() {
       this.maxVisitorsValue = Integer.MIN_VALUE;
       this.minVisitorsValue = Integer.MAX_VALUE;
       this.maxExtraCostPerPersonValue = Double.MIN_VALUE;
       this.minExtraCostPerPersonValue = Double.MAX_VALUE;
       this.maxRetailPriceValue = Double.MIN_VALUE;
       this.minRetailPriceValue = Double.MAX_VALUE;
       this.maxNumberOfPlacesValue = Double.MIN_VALUE;
       this.minNumberOfPlacesValue = Double.MAX_VALUE;
    }

    public Integer maxVisitorsValue;
    public Integer minVisitorsValue;
    public double maxExtraCostPerPersonValue;
    public double minExtraCostPerPersonValue;

    public double minRetailPriceValue;
    public double maxRetailPriceValue;

    public double minNumberOfPlacesValue;
    public double maxNumberOfPlacesValue;

    public void compMaxVisitorsValue(Integer val) {
        if (val > maxVisitorsValue) maxVisitorsValue = val;
    }

    public void compMinVisitorsValue(Integer val) {
        if (minVisitorsValue > val) minVisitorsValue = val;
    }

    public void compMaxExtraCostPerPersonValue(double val) {
        if (val > maxExtraCostPerPersonValue) maxExtraCostPerPersonValue = val;
    }

    public void compMinExtraCostPerPersonValue(double val) {
        if (minExtraCostPerPersonValue > val) minExtraCostPerPersonValue = val;
    }

    public void compMaxRetailPriceValue(double val) {
        if (val > maxRetailPriceValue) maxRetailPriceValue = val;
    }

    public void compMinRetailPriceValue(double val) {
        if (minRetailPriceValue > val) minRetailPriceValue = val;
    }

    public void compMaxNumberOfPlacesValue(double val) {
        if (val > maxNumberOfPlacesValue) maxNumberOfPlacesValue = val;
    }

    public void compMinNumberOfPlacesValue(double val) {
        if (minNumberOfPlacesValue > val) minNumberOfPlacesValue = val;
    }

}
