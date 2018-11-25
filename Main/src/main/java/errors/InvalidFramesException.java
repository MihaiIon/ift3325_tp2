package errors;

import models.FrameModel;

import java.util.ArrayList;

public class InvalidFramesException extends Exception {

    private ArrayList<FrameModel> frameModels;
    private String invalidStream;

    public InvalidFramesException(ArrayList<FrameModel> frameModels, String invalidStream) {
        this.frameModels = frameModels;
        this.invalidStream = invalidStream;
    }

    public ArrayList<FrameModel> getFrameModels() {
        return frameModels;
    }

    public String getInvalidStream() {
        return invalidStream;
    }
}
