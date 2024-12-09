public class PrivacySettings {
    private int emailVisibility; // 1: everyone, 2: friends only, 3: hidden

    public PrivacySettings() {
        this.emailVisibility = 2; // default to friends only
    }

    public void setEmailVisibility(int visibility) {
        if (visibility >= 1 && visibility <= 3) {
            this.emailVisibility = visibility;
        }
    }

    public int getEmailVisibility() {
        return emailVisibility;
    }
} 