package com.tmjee.evo.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tmjee
 */
public class Input {
    private final Map<String, String> params;
    private String result;

    Input(Map<String, String> params) {
        this.params = params;
    }

    public Input setResult(String result) {
        this.result = result;
        return this;
    }

    public String getResult() {
        return result;
    }


    public static class Builder {
        private Map<String, String> params = new HashMap<>();

        public Builder setInput(String name, String value) {
            return this;
        }

        public Input build() {
            return new Input(params);
        }
    }
}
