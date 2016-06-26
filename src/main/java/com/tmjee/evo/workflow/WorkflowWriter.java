package com.tmjee.evo.workflow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author tmjee
 */
public class WorkflowWriter {

    public void write(Workflow workflow, OutputStream os) throws ParserConfigurationException, TransformerException {

        Workflow.Internals internals = workflow._internals();
        Map<String, WorkflowStep> m =  internals.m; // Map<String, WorkflowStep>
        WorkflowContext wc = internals.wc;
        Input i = internals.i;

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element workflowElement = document.createElement("workflow");
        document.appendChild(workflowElement);
        Element workflowSteps = document.createElement("steps");
        workflowElement.appendChild(workflowSteps);
        Element workflowContext = document.createElement("context");
        workflowElement.appendChild(workflowContext);
        Element workflowInput = document.createElement("input");
        workflowElement.appendChild(workflowInput);

        // workflow steps
        for (WorkflowStep step : m.values()) {
            Element workflowStep = document.createElement("step");
            workflowStep.setAttribute("name", step.getName());
            workflowStep.setAttribute("type", step.getType().name());
            workflowSteps.appendChild(workflowStep);
        }

        // workflow context
        Element nextWorkflowStepName = document.createElement("nextWorkflowStepName");
        nextWorkflowStepName.setTextContent(wc.getNextWorkflowStepName());
        workflowContext.appendChild(nextWorkflowStepName);
        Element userSpace = document.createElement("userSpace");
        workflowContext.appendChild(userSpace);
        for (Map.Entry<String, String> entry : wc.getUserSpace().entrySet()) {
            Element param = document.createElement("param");
            param.setAttribute("name", entry.getKey());
            param.setTextContent(entry.getValue());
            userSpace.appendChild(param);
        }


        // input
        Element result = document.createElement("result");
        workflowInput.appendChild(result);
        Element params = document.createElement("params");
        workflowInput.appendChild(params);
        if (i != null) {
            if (i.getResult() != null) {
                result.setTextContent(i.getResult());
            }
            for (Map.Entry<String, String> p : i.getParams().entrySet()) {
                Element param = document.createElement("param");
                param.setAttribute("name", p.getKey());
                param.setTextContent(p.getValue());
                params.appendChild(param);
            }
        }


        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(document), new StreamResult(os));
    }
}
