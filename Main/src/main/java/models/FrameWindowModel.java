package models;

import java.util.Arrays;

public class FrameWindowModel {

    // Attributes
    private final FrameModel[] frames;

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
            if(frames[0] == null) {
                frames[0] = frame;
                return true;
            }
            System.out.println("ERROR : Frame #" + id + " has been already received.");
            return false;
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
        return true;
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

    public int getNextPosition() {
        for(int i = 0; i < frames.length; i++) {
            if(frames[i] == null) {
                return i;
            }
        }
        return frames.length + 1;
    }

    public int getPosition() {
        for(int i = 0; i < frames.length; i++) {
            if(frames[i] == null) {
                if(i == 0) {
                    return 7;
                }
                return i-1;
            }
        }
        return frames.length;
    }
}
