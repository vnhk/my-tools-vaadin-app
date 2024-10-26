package com.bervan.toolsapp.views.learninglanguage;

class TranslationRecordRequest {
    String englishText;
    String polishText;
    Boolean saveWithSound;
    Boolean generateExample;

    public Boolean getGenerateExample() {
        return generateExample;
    }

    public void setGenerateExample(Boolean generateExample) {
        this.generateExample = generateExample;
    }

    public String getEnglishText() {
        return englishText;
    }

    public void setEnglishText(String englishText) {
        this.englishText = englishText;
    }

    public String getPolishText() {
        return polishText;
    }

    public void setPolishText(String polishText) {
        this.polishText = polishText;
    }

    public boolean getSaveWithSound() {
        return saveWithSound;
    }

    public void setSaveWithSound(Boolean saveWithSound) {
        this.saveWithSound = saveWithSound;
    }
}
