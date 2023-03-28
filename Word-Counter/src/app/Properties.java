package app;

public enum Properties {

    KEYWORDS,
    FILE_CORPUS_PREFIX,
    HOP_COUNT,
    FILE_SCANNING_SIZE_LIMIT,
    BUFFER_TIMEOUT,
    DIR_CRAWLER_SLEEP_TIME,
    URL_REFRESH_TIME;

    private String value;

    public String get() {
        return value;
    }

    public void set(String value) {
        if(this.value == null && value != null) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " = " + get();
    }
}
