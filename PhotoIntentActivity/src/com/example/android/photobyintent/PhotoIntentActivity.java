package com.example.android.photobyintent;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PhotoIntentActivity extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = "Preview";
	SurfaceHolder mHolder;
	public Camera camera;

	PhotoIntentActivity(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.RGB_332);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewCallback(new PreviewCallback() {
				public void onPreviewFrame(byte[] _data, Camera _camera) {
					int width = 320;
					int height = 240;

					eth = getInterfaces();
					Log.v("Connected to ", "Ethernet" + eth);

					if (eth != null) {
						try {

							InetAddress serverAddr = InetAddress
									.getByName("IP Address of My PC");

							Log.v("trying to ", "connect with" + serverAddr);

							SipdroidSocket soc = new SipdroidSocket(9954);

							Log.v("trying to ", "connect with Sipdroid Socket");

							soc.connect(serverAddr, 9954);

							Log.v("Socket ", "Connected");

							RtpPacket rtpp = new RtpPacket(_data, height);

							// rtpp.setPayloadType(125);

							Log.v("RTPPacket", "Created");

							RtpSocket rtps = new RtpSocket(soc, serverAddr,
									9954);

							Log.v("RTPSocket", "Created");

							rtps.send(rtpp);

							Log.v("Packet", "Sent");

						}

						catch (Exception e) {
							e.printStackTrace();
							Log.v(TAG, "Socket");
						}
					}
				}

			});

			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			Log.d("CAMERA", e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		camera.stopPreview();
		camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(80, 60);
		camera.setParameters(parameters);

		Thread preview_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				camera.startPreview();
			}
		}, "preview_thread");
		preview_thread.start();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.d(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
				canvas.getHeight() / 2, p);
	}

}