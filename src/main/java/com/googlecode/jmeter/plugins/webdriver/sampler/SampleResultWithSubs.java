package com.googlecode.jmeter.plugins.webdriver.sampler;

import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleResultWithSubs extends SampleResult {
	private static final long serialVersionUID = 100L;
	private static final Logger log = LoggerFactory.getLogger(SampleResultWithSubs.class);
    private SampleResult subSample;

    public void subSampleStart(String label) {
        if (subSample != null) {
            log.warn("There is already a sub-sample started, continuing using it");
            return;
        }

        if (getStartTime() == 0) {
            sampleStart();
        }

        subSample = new SampleResult();
        subSample.setSampleLabel(label);
        subSample.setDataType(SampleResult.TEXT);
        subSample.setSuccessful(true);
        subSample.sampleStart();
    }

    public void subSampleEnd(boolean success) {
        if (subSample == null) {
            log.warn("There is no sub-sample started, use subSampleStart() to have one");
            return;
        }
        subSample.sampleEnd();
        subSample.setSuccessful(success);
        super.addSubResult(subSample);
        subSample = null;
    }

    @Override
    public void sampleEnd() {
        if (subSample != null) {
            subSampleEnd(true);
        }

        if (getEndTime() == 0) {
            super.sampleEnd();
        }
    }
}
