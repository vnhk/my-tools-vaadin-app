package com.bervan.toolsapp.views.learninglanguage;

class TranslationRecordRequest {
    private String englishText;
    private String polishText;
    private Boolean saveWithSound;
    private Boolean generateExample;
    private String apiKey;

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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
