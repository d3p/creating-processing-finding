package de.hfkbremen.algorithmiccliches.util;

import processing.core.PApplet;
import processing.core.PVector;
import teilchen.util.Vector4f;

/**
 * http://de.wikipedia.org/wiki/Rolling-Ball-Rotation
 */
public class ACArcBall {

    private final PApplet mParent;
    private final PVector mCenter;
    private final PVector mDownPosition;
    private final PVector mDragPosition;
    private final Quaternion mCurrentQuaternion;
    private final Quaternion mDownQuaternion;
    private final Quaternion mDragQuaternion;
    private float mRadius;
    private boolean mLastActiveState = false;

    public ACArcBall(PApplet pApplet, boolean pDONT_REGISTER) {
        this(pApplet.g.width / 2.0f,
             pApplet.g.height / 2.0f,
             -Math.min(pApplet.g.width / 2.0f, pApplet.g.height / 2.0f),
             Math.min(pApplet.g.width / 2.0f, pApplet.g.height / 2.0f),
             pApplet,
             pDONT_REGISTER);
    }

    public ACArcBall(PApplet parent) {
        this(parent, false);
    }

    public ACArcBall(float theCenterX,
                     float theCenterY,
                     float theCenterZ,
                     float theRadius,
                     PApplet pApplet,
                     boolean pDO_NOT_REGISTER) {
        this(new PVector(theCenterX, theCenterY, theCenterZ), theRadius, pApplet, pDO_NOT_REGISTER);
    }

    public ACArcBall(final PVector theCenter, final float theRadius, final PApplet pApplet, boolean pDO_NOT_REGISTER) {

        mParent = pApplet;

        if (!pDO_NOT_REGISTER) {
            //            pApplet.registerPre(this);
            pApplet.registerMethod("pre", this); // new in processing 3.0 @test
        }

        mCenter = theCenter;
        mRadius = theRadius;

        mDownPosition = new PVector();
        mDragPosition = new PVector();

        mCurrentQuaternion = new Quaternion();
        mDownQuaternion = new Quaternion();
        mDragQuaternion = new Quaternion();
    }

    public static ACArcBall setupRotateAroundCenter(PApplet pApplet) {
        return new ACArcBall(pApplet.width / 2.0f,
                             pApplet.height / 2.0f,
                             0,
                             Math.min(pApplet.g.width / 2.0f, pApplet.g.height / 2.0f),
                             pApplet,
                             false);
    }

    public void mousePressed(float theX, float theY) {
        mDownPosition.set(mouse_to_sphere(theX, theY));
        mDownQuaternion.set(mCurrentQuaternion);
        mDragQuaternion.reset();
    }

    public void mouseDragged(float theX, float theY) {
        mDragPosition.set(mouse_to_sphere(theX, theY));
        mDragQuaternion.set(mDownPosition.dot(mDragPosition), PVector.cross(mDownPosition, mDragPosition, null));
    }

    public void update() {
        update(mParent.mousePressed, mParent.mouseX, mParent.mouseY);
    }

    public void update(boolean theActiveState, float theX, float theY) {
        if (mParent == null) {
            return;
        }

        if (theActiveState) {
            if (!mLastActiveState) {
                mousePressed(theX, theY);
            }
            mouseDragged(theX, theY);
        }
        mLastActiveState = theActiveState;


        /* apply transform */
        mParent.translate(mCenter.x, mCenter.y, mCenter.z);
        mCurrentQuaternion.multiply(mDragQuaternion, mDownQuaternion);
        final Vector4f myRotationAxisAngle = mCurrentQuaternion.getVectorAndAngle();
        if (!myRotationAxisAngle.isNaN()) {
            mParent.rotate(myRotationAxisAngle.w, myRotationAxisAngle.x, myRotationAxisAngle.y, myRotationAxisAngle.z);
        }
        mParent.translate(-mCenter.x, -mCenter.y, -mCenter.z);
    }

    public void radius(float pRadius) {
        mRadius = pRadius;
    }

    public PVector center() {
        return mCenter;
    }

    /* processing callbacks */
    public void pre() {
        update();
    }

    private PVector mouse_to_sphere(float x, float y) {
        final PVector v = new PVector();
        v.x = (x - mCenter.x) / mRadius;
        v.y = (y - mCenter.y) / mRadius;

        float myLengthSquared = v.x * v.x + v.y * v.y;
        if (myLengthSquared > 1.0f) {
            v.normalize();
        } else {
            v.z = (float) Math.sqrt(1.0f - myLengthSquared);
        }
        return v;
    }
}
