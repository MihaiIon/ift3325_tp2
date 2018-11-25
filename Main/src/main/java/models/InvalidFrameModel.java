package models;

public class InvalidFrameModel extends FrameModel {

    public InvalidFrameModel(String frameStream) {

    }

    @Override
    public boolean hasErrors() {
        return true;
    }
}
