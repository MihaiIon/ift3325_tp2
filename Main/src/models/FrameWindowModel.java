package models;

public class FrameWindowModel {

    // Attributes
    private FrameModel[] frames;

    /**
     *
     */
    public FrameWindowModel() {
        frames = new FrameModel[8];
    }

    // ------------------------------------------------------------------------
    // Methods

    /**
     *
     * @param frame
     * @return
     */
    public boolean addFrame(FrameModel frame) {
        int id = ((InformationFrameModel) frame).getId();
        if (id == 0) {
            frames[0] = frame;
            return true;
        }
        if (frames[id-1] == null) {
            System.out.println("ERROR : Missing frame #" + (id-1) + ".");
            return false;
        }
        if (frames[id] != null) {
            System.out.println("ERROR : Frame #" + id + " has been already received.");
            return false;
        }
        frames[id] = frame;
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isFull() {
        for (FrameModel frame : frames) {
            if(frame == null) return false;
        }
        return true;
    }

    // ------------------------------------------------------------------------
    // Getters

    public FrameModel[] getFrames() { return frames; }
}
