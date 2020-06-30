package org.lwjgl.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLException;
import org.lwjgl.MemoryUtil;

public final class EFX10 {
   public static final String ALC_EXT_EFX_NAME = "ALC_EXT_EFX";
   public static final int ALC_EFX_MAJOR_VERSION = 131073;
   public static final int ALC_EFX_MINOR_VERSION = 131074;
   public static final int ALC_MAX_AUXILIARY_SENDS = 131075;
   public static final int AL_METERS_PER_UNIT = 131076;
   public static final int AL_DIRECT_FILTER = 131077;
   public static final int AL_AUXILIARY_SEND_FILTER = 131078;
   public static final int AL_AIR_ABSORPTION_FACTOR = 131079;
   public static final int AL_ROOM_ROLLOFF_FACTOR = 131080;
   public static final int AL_CONE_OUTER_GAINHF = 131081;
   public static final int AL_DIRECT_FILTER_GAINHF_AUTO = 131082;
   public static final int AL_AUXILIARY_SEND_FILTER_GAIN_AUTO = 131083;
   public static final int AL_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 131084;
   public static final int AL_EFFECTSLOT_EFFECT = 1;
   public static final int AL_EFFECTSLOT_GAIN = 2;
   public static final int AL_EFFECTSLOT_AUXILIARY_SEND_AUTO = 3;
   public static final int AL_EFFECTSLOT_NULL = 0;
   public static final int AL_REVERB_DENSITY = 1;
   public static final int AL_REVERB_DIFFUSION = 2;
   public static final int AL_REVERB_GAIN = 3;
   public static final int AL_REVERB_GAINHF = 4;
   public static final int AL_REVERB_DECAY_TIME = 5;
   public static final int AL_REVERB_DECAY_HFRATIO = 6;
   public static final int AL_REVERB_REFLECTIONS_GAIN = 7;
   public static final int AL_REVERB_REFLECTIONS_DELAY = 8;
   public static final int AL_REVERB_LATE_REVERB_GAIN = 9;
   public static final int AL_REVERB_LATE_REVERB_DELAY = 10;
   public static final int AL_REVERB_AIR_ABSORPTION_GAINHF = 11;
   public static final int AL_REVERB_ROOM_ROLLOFF_FACTOR = 12;
   public static final int AL_REVERB_DECAY_HFLIMIT = 13;
   public static final int AL_EAXREVERB_DENSITY = 1;
   public static final int AL_EAXREVERB_DIFFUSION = 2;
   public static final int AL_EAXREVERB_GAIN = 3;
   public static final int AL_EAXREVERB_GAINHF = 4;
   public static final int AL_EAXREVERB_GAINLF = 5;
   public static final int AL_EAXREVERB_DECAY_TIME = 6;
   public static final int AL_EAXREVERB_DECAY_HFRATIO = 7;
   public static final int AL_EAXREVERB_DECAY_LFRATIO = 8;
   public static final int AL_EAXREVERB_REFLECTIONS_GAIN = 9;
   public static final int AL_EAXREVERB_REFLECTIONS_DELAY = 10;
   public static final int AL_EAXREVERB_REFLECTIONS_PAN = 11;
   public static final int AL_EAXREVERB_LATE_REVERB_GAIN = 12;
   public static final int AL_EAXREVERB_LATE_REVERB_DELAY = 13;
   public static final int AL_EAXREVERB_LATE_REVERB_PAN = 14;
   public static final int AL_EAXREVERB_ECHO_TIME = 15;
   public static final int AL_EAXREVERB_ECHO_DEPTH = 16;
   public static final int AL_EAXREVERB_MODULATION_TIME = 17;
   public static final int AL_EAXREVERB_MODULATION_DEPTH = 18;
   public static final int AL_EAXREVERB_AIR_ABSORPTION_GAINHF = 19;
   public static final int AL_EAXREVERB_HFREFERENCE = 20;
   public static final int AL_EAXREVERB_LFREFERENCE = 21;
   public static final int AL_EAXREVERB_ROOM_ROLLOFF_FACTOR = 22;
   public static final int AL_EAXREVERB_DECAY_HFLIMIT = 23;
   public static final int AL_CHORUS_WAVEFORM = 1;
   public static final int AL_CHORUS_PHASE = 2;
   public static final int AL_CHORUS_RATE = 3;
   public static final int AL_CHORUS_DEPTH = 4;
   public static final int AL_CHORUS_FEEDBACK = 5;
   public static final int AL_CHORUS_DELAY = 6;
   public static final int AL_DISTORTION_EDGE = 1;
   public static final int AL_DISTORTION_GAIN = 2;
   public static final int AL_DISTORTION_LOWPASS_CUTOFF = 3;
   public static final int AL_DISTORTION_EQCENTER = 4;
   public static final int AL_DISTORTION_EQBANDWIDTH = 5;
   public static final int AL_ECHO_DELAY = 1;
   public static final int AL_ECHO_LRDELAY = 2;
   public static final int AL_ECHO_DAMPING = 3;
   public static final int AL_ECHO_FEEDBACK = 4;
   public static final int AL_ECHO_SPREAD = 5;
   public static final int AL_FLANGER_WAVEFORM = 1;
   public static final int AL_FLANGER_PHASE = 2;
   public static final int AL_FLANGER_RATE = 3;
   public static final int AL_FLANGER_DEPTH = 4;
   public static final int AL_FLANGER_FEEDBACK = 5;
   public static final int AL_FLANGER_DELAY = 6;
   public static final int AL_FREQUENCY_SHIFTER_FREQUENCY = 1;
   public static final int AL_FREQUENCY_SHIFTER_LEFT_DIRECTION = 2;
   public static final int AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION = 3;
   public static final int AL_VOCAL_MORPHER_PHONEMEA = 1;
   public static final int AL_VOCAL_MORPHER_PHONEMEA_COARSE_TUNING = 2;
   public static final int AL_VOCAL_MORPHER_PHONEMEB = 3;
   public static final int AL_VOCAL_MORPHER_PHONEMEB_COARSE_TUNING = 4;
   public static final int AL_VOCAL_MORPHER_WAVEFORM = 5;
   public static final int AL_VOCAL_MORPHER_RATE = 6;
   public static final int AL_PITCH_SHIFTER_COARSE_TUNE = 1;
   public static final int AL_PITCH_SHIFTER_FINE_TUNE = 2;
   public static final int AL_RING_MODULATOR_FREQUENCY = 1;
   public static final int AL_RING_MODULATOR_HIGHPASS_CUTOFF = 2;
   public static final int AL_RING_MODULATOR_WAVEFORM = 3;
   public static final int AL_AUTOWAH_ATTACK_TIME = 1;
   public static final int AL_AUTOWAH_RELEASE_TIME = 2;
   public static final int AL_AUTOWAH_RESONANCE = 3;
   public static final int AL_AUTOWAH_PEAK_GAIN = 4;
   public static final int AL_COMPRESSOR_ONOFF = 1;
   public static final int AL_EQUALIZER_LOW_GAIN = 1;
   public static final int AL_EQUALIZER_LOW_CUTOFF = 2;
   public static final int AL_EQUALIZER_MID1_GAIN = 3;
   public static final int AL_EQUALIZER_MID1_CENTER = 4;
   public static final int AL_EQUALIZER_MID1_WIDTH = 5;
   public static final int AL_EQUALIZER_MID2_GAIN = 6;
   public static final int AL_EQUALIZER_MID2_CENTER = 7;
   public static final int AL_EQUALIZER_MID2_WIDTH = 8;
   public static final int AL_EQUALIZER_HIGH_GAIN = 9;
   public static final int AL_EQUALIZER_HIGH_CUTOFF = 10;
   public static final int AL_EFFECT_FIRST_PARAMETER = 0;
   public static final int AL_EFFECT_LAST_PARAMETER = 32768;
   public static final int AL_EFFECT_TYPE = 32769;
   public static final int AL_EFFECT_NULL = 0;
   public static final int AL_EFFECT_REVERB = 1;
   public static final int AL_EFFECT_CHORUS = 2;
   public static final int AL_EFFECT_DISTORTION = 3;
   public static final int AL_EFFECT_ECHO = 4;
   public static final int AL_EFFECT_FLANGER = 5;
   public static final int AL_EFFECT_FREQUENCY_SHIFTER = 6;
   public static final int AL_EFFECT_VOCAL_MORPHER = 7;
   public static final int AL_EFFECT_PITCH_SHIFTER = 8;
   public static final int AL_EFFECT_RING_MODULATOR = 9;
   public static final int AL_EFFECT_AUTOWAH = 10;
   public static final int AL_EFFECT_COMPRESSOR = 11;
   public static final int AL_EFFECT_EQUALIZER = 12;
   public static final int AL_EFFECT_EAXREVERB = 32768;
   public static final int AL_LOWPASS_GAIN = 1;
   public static final int AL_LOWPASS_GAINHF = 2;
   public static final int AL_HIGHPASS_GAIN = 1;
   public static final int AL_HIGHPASS_GAINLF = 2;
   public static final int AL_BANDPASS_GAIN = 1;
   public static final int AL_BANDPASS_GAINLF = 2;
   public static final int AL_BANDPASS_GAINHF = 3;
   public static final int AL_FILTER_FIRST_PARAMETER = 0;
   public static final int AL_FILTER_LAST_PARAMETER = 32768;
   public static final int AL_FILTER_TYPE = 32769;
   public static final int AL_FILTER_NULL = 0;
   public static final int AL_FILTER_LOWPASS = 1;
   public static final int AL_FILTER_HIGHPASS = 2;
   public static final int AL_FILTER_BANDPASS = 3;
   public static final float AL_MIN_AIR_ABSORPTION_FACTOR = 0.0F;
   public static final float AL_MAX_AIR_ABSORPTION_FACTOR = 10.0F;
   public static final float AL_DEFAULT_AIR_ABSORPTION_FACTOR = 0.0F;
   public static final float AL_MIN_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final float AL_MAX_ROOM_ROLLOFF_FACTOR = 10.0F;
   public static final float AL_DEFAULT_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final float AL_MIN_CONE_OUTER_GAINHF = 0.0F;
   public static final float AL_MAX_CONE_OUTER_GAINHF = 1.0F;
   public static final float AL_DEFAULT_CONE_OUTER_GAINHF = 1.0F;
   public static final int AL_MIN_DIRECT_FILTER_GAINHF_AUTO = 0;
   public static final int AL_MAX_DIRECT_FILTER_GAINHF_AUTO = 1;
   public static final int AL_DEFAULT_DIRECT_FILTER_GAINHF_AUTO = 1;
   public static final int AL_MIN_AUXILIARY_SEND_FILTER_GAIN_AUTO = 0;
   public static final int AL_MAX_AUXILIARY_SEND_FILTER_GAIN_AUTO = 1;
   public static final int AL_DEFAULT_AUXILIARY_SEND_FILTER_GAIN_AUTO = 1;
   public static final int AL_MIN_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 0;
   public static final int AL_MAX_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 1;
   public static final int AL_DEFAULT_AUXILIARY_SEND_FILTER_GAINHF_AUTO = 1;
   public static final float AL_MIN_METERS_PER_UNIT = Float.MIN_VALUE;
   public static final float AL_MAX_METERS_PER_UNIT = Float.MAX_VALUE;
   public static final float AL_DEFAULT_METERS_PER_UNIT = 1.0F;
   public static final float AL_REVERB_MIN_DENSITY = 0.0F;
   public static final float AL_REVERB_MAX_DENSITY = 1.0F;
   public static final float AL_REVERB_DEFAULT_DENSITY = 1.0F;
   public static final float AL_REVERB_MIN_DIFFUSION = 0.0F;
   public static final float AL_REVERB_MAX_DIFFUSION = 1.0F;
   public static final float AL_REVERB_DEFAULT_DIFFUSION = 1.0F;
   public static final float AL_REVERB_MIN_GAIN = 0.0F;
   public static final float AL_REVERB_MAX_GAIN = 1.0F;
   public static final float AL_REVERB_DEFAULT_GAIN = 0.32F;
   public static final float AL_REVERB_MIN_GAINHF = 0.0F;
   public static final float AL_REVERB_MAX_GAINHF = 1.0F;
   public static final float AL_REVERB_DEFAULT_GAINHF = 0.89F;
   public static final float AL_REVERB_MIN_DECAY_TIME = 0.1F;
   public static final float AL_REVERB_MAX_DECAY_TIME = 20.0F;
   public static final float AL_REVERB_DEFAULT_DECAY_TIME = 1.49F;
   public static final float AL_REVERB_MIN_DECAY_HFRATIO = 0.1F;
   public static final float AL_REVERB_MAX_DECAY_HFRATIO = 2.0F;
   public static final float AL_REVERB_DEFAULT_DECAY_HFRATIO = 0.83F;
   public static final float AL_REVERB_MIN_REFLECTIONS_GAIN = 0.0F;
   public static final float AL_REVERB_MAX_REFLECTIONS_GAIN = 3.16F;
   public static final float AL_REVERB_DEFAULT_REFLECTIONS_GAIN = 0.05F;
   public static final float AL_REVERB_MIN_REFLECTIONS_DELAY = 0.0F;
   public static final float AL_REVERB_MAX_REFLECTIONS_DELAY = 0.3F;
   public static final float AL_REVERB_DEFAULT_REFLECTIONS_DELAY = 0.007F;
   public static final float AL_REVERB_MIN_LATE_REVERB_GAIN = 0.0F;
   public static final float AL_REVERB_MAX_LATE_REVERB_GAIN = 10.0F;
   public static final float AL_REVERB_DEFAULT_LATE_REVERB_GAIN = 1.26F;
   public static final float AL_REVERB_MIN_LATE_REVERB_DELAY = 0.0F;
   public static final float AL_REVERB_MAX_LATE_REVERB_DELAY = 0.1F;
   public static final float AL_REVERB_DEFAULT_LATE_REVERB_DELAY = 0.011F;
   public static final float AL_REVERB_MIN_AIR_ABSORPTION_GAINHF = 0.892F;
   public static final float AL_REVERB_MAX_AIR_ABSORPTION_GAINHF = 1.0F;
   public static final float AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF = 0.994F;
   public static final float AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final float AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR = 10.0F;
   public static final float AL_REVERB_DEFAULT_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final int AL_REVERB_MIN_DECAY_HFLIMIT = 0;
   public static final int AL_REVERB_MAX_DECAY_HFLIMIT = 1;
   public static final int AL_REVERB_DEFAULT_DECAY_HFLIMIT = 1;
   public static final float AL_EAXREVERB_MIN_DENSITY = 0.0F;
   public static final float AL_EAXREVERB_MAX_DENSITY = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_DENSITY = 1.0F;
   public static final float AL_EAXREVERB_MIN_DIFFUSION = 0.0F;
   public static final float AL_EAXREVERB_MAX_DIFFUSION = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_DIFFUSION = 1.0F;
   public static final float AL_EAXREVERB_MIN_GAIN = 0.0F;
   public static final float AL_EAXREVERB_MAX_GAIN = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_GAIN = 0.32F;
   public static final float AL_EAXREVERB_MIN_GAINHF = 0.0F;
   public static final float AL_EAXREVERB_MAX_GAINHF = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_GAINHF = 0.89F;
   public static final float AL_EAXREVERB_MIN_GAINLF = 0.0F;
   public static final float AL_EAXREVERB_MAX_GAINLF = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_GAINLF = 1.0F;
   public static final float AL_EAXREVERB_MIN_DECAY_TIME = 0.1F;
   public static final float AL_EAXREVERB_MAX_DECAY_TIME = 20.0F;
   public static final float AL_EAXREVERB_DEFAULT_DECAY_TIME = 1.49F;
   public static final float AL_EAXREVERB_MIN_DECAY_HFRATIO = 0.1F;
   public static final float AL_EAXREVERB_MAX_DECAY_HFRATIO = 2.0F;
   public static final float AL_EAXREVERB_DEFAULT_DECAY_HFRATIO = 0.83F;
   public static final float AL_EAXREVERB_MIN_DECAY_LFRATIO = 0.1F;
   public static final float AL_EAXREVERB_MAX_DECAY_LFRATIO = 2.0F;
   public static final float AL_EAXREVERB_DEFAULT_DECAY_LFRATIO = 1.0F;
   public static final float AL_EAXREVERB_MIN_REFLECTIONS_GAIN = 0.0F;
   public static final float AL_EAXREVERB_MAX_REFLECTIONS_GAIN = 3.16F;
   public static final float AL_EAXREVERB_DEFAULT_REFLECTIONS_GAIN = 0.05F;
   public static final float AL_EAXREVERB_MIN_REFLECTIONS_DELAY = 0.0F;
   public static final float AL_EAXREVERB_MAX_REFLECTIONS_DELAY = 0.3F;
   public static final float AL_EAXREVERB_DEFAULT_REFLECTIONS_DELAY = 0.007F;
   public static final float AL_EAXREVERB_DEFAULT_REFLECTIONS_PAN_XYZ = 0.0F;
   public static final float AL_EAXREVERB_MIN_LATE_REVERB_GAIN = 0.0F;
   public static final float AL_EAXREVERB_MAX_LATE_REVERB_GAIN = 10.0F;
   public static final float AL_EAXREVERB_DEFAULT_LATE_REVERB_GAIN = 1.26F;
   public static final float AL_EAXREVERB_MIN_LATE_REVERB_DELAY = 0.0F;
   public static final float AL_EAXREVERB_MAX_LATE_REVERB_DELAY = 0.1F;
   public static final float AL_EAXREVERB_DEFAULT_LATE_REVERB_DELAY = 0.011F;
   public static final float AL_EAXREVERB_DEFAULT_LATE_REVERB_PAN_XYZ = 0.0F;
   public static final float AL_EAXREVERB_MIN_ECHO_TIME = 0.075F;
   public static final float AL_EAXREVERB_MAX_ECHO_TIME = 0.25F;
   public static final float AL_EAXREVERB_DEFAULT_ECHO_TIME = 0.25F;
   public static final float AL_EAXREVERB_MIN_ECHO_DEPTH = 0.0F;
   public static final float AL_EAXREVERB_MAX_ECHO_DEPTH = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_ECHO_DEPTH = 0.0F;
   public static final float AL_EAXREVERB_MIN_MODULATION_TIME = 0.04F;
   public static final float AL_EAXREVERB_MAX_MODULATION_TIME = 4.0F;
   public static final float AL_EAXREVERB_DEFAULT_MODULATION_TIME = 0.25F;
   public static final float AL_EAXREVERB_MIN_MODULATION_DEPTH = 0.0F;
   public static final float AL_EAXREVERB_MAX_MODULATION_DEPTH = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_MODULATION_DEPTH = 0.0F;
   public static final float AL_EAXREVERB_MIN_AIR_ABSORPTION_GAINHF = 0.892F;
   public static final float AL_EAXREVERB_MAX_AIR_ABSORPTION_GAINHF = 1.0F;
   public static final float AL_EAXREVERB_DEFAULT_AIR_ABSORPTION_GAINHF = 0.994F;
   public static final float AL_EAXREVERB_MIN_HFREFERENCE = 1000.0F;
   public static final float AL_EAXREVERB_MAX_HFREFERENCE = 20000.0F;
   public static final float AL_EAXREVERB_DEFAULT_HFREFERENCE = 5000.0F;
   public static final float AL_EAXREVERB_MIN_LFREFERENCE = 20.0F;
   public static final float AL_EAXREVERB_MAX_LFREFERENCE = 1000.0F;
   public static final float AL_EAXREVERB_DEFAULT_LFREFERENCE = 250.0F;
   public static final float AL_EAXREVERB_MIN_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final float AL_EAXREVERB_MAX_ROOM_ROLLOFF_FACTOR = 10.0F;
   public static final float AL_EAXREVERB_DEFAULT_ROOM_ROLLOFF_FACTOR = 0.0F;
   public static final int AL_EAXREVERB_MIN_DECAY_HFLIMIT = 0;
   public static final int AL_EAXREVERB_MAX_DECAY_HFLIMIT = 1;
   public static final int AL_EAXREVERB_DEFAULT_DECAY_HFLIMIT = 1;
   public static final int AL_CHORUS_WAVEFORM_SINUSOID = 0;
   public static final int AL_CHORUS_WAVEFORM_TRIANGLE = 1;
   public static final int AL_CHORUS_MIN_WAVEFORM = 0;
   public static final int AL_CHORUS_MAX_WAVEFORM = 1;
   public static final int AL_CHORUS_DEFAULT_WAVEFORM = 1;
   public static final int AL_CHORUS_MIN_PHASE = -180;
   public static final int AL_CHORUS_MAX_PHASE = 180;
   public static final int AL_CHORUS_DEFAULT_PHASE = 90;
   public static final float AL_CHORUS_MIN_RATE = 0.0F;
   public static final float AL_CHORUS_MAX_RATE = 10.0F;
   public static final float AL_CHORUS_DEFAULT_RATE = 1.1F;
   public static final float AL_CHORUS_MIN_DEPTH = 0.0F;
   public static final float AL_CHORUS_MAX_DEPTH = 1.0F;
   public static final float AL_CHORUS_DEFAULT_DEPTH = 0.1F;
   public static final float AL_CHORUS_MIN_FEEDBACK = -1.0F;
   public static final float AL_CHORUS_MAX_FEEDBACK = 1.0F;
   public static final float AL_CHORUS_DEFAULT_FEEDBACK = 0.25F;
   public static final float AL_CHORUS_MIN_DELAY = 0.0F;
   public static final float AL_CHORUS_MAX_DELAY = 0.016F;
   public static final float AL_CHORUS_DEFAULT_DELAY = 0.016F;
   public static final float AL_DISTORTION_MIN_EDGE = 0.0F;
   public static final float AL_DISTORTION_MAX_EDGE = 1.0F;
   public static final float AL_DISTORTION_DEFAULT_EDGE = 0.2F;
   public static final float AL_DISTORTION_MIN_GAIN = 0.01F;
   public static final float AL_DISTORTION_MAX_GAIN = 1.0F;
   public static final float AL_DISTORTION_DEFAULT_GAIN = 0.05F;
   public static final float AL_DISTORTION_MIN_LOWPASS_CUTOFF = 80.0F;
   public static final float AL_DISTORTION_MAX_LOWPASS_CUTOFF = 24000.0F;
   public static final float AL_DISTORTION_DEFAULT_LOWPASS_CUTOFF = 8000.0F;
   public static final float AL_DISTORTION_MIN_EQCENTER = 80.0F;
   public static final float AL_DISTORTION_MAX_EQCENTER = 24000.0F;
   public static final float AL_DISTORTION_DEFAULT_EQCENTER = 3600.0F;
   public static final float AL_DISTORTION_MIN_EQBANDWIDTH = 80.0F;
   public static final float AL_DISTORTION_MAX_EQBANDWIDTH = 24000.0F;
   public static final float AL_DISTORTION_DEFAULT_EQBANDWIDTH = 3600.0F;
   public static final float AL_ECHO_MIN_DELAY = 0.0F;
   public static final float AL_ECHO_MAX_DELAY = 0.207F;
   public static final float AL_ECHO_DEFAULT_DELAY = 0.1F;
   public static final float AL_ECHO_MIN_LRDELAY = 0.0F;
   public static final float AL_ECHO_MAX_LRDELAY = 0.404F;
   public static final float AL_ECHO_DEFAULT_LRDELAY = 0.1F;
   public static final float AL_ECHO_MIN_DAMPING = 0.0F;
   public static final float AL_ECHO_MAX_DAMPING = 0.99F;
   public static final float AL_ECHO_DEFAULT_DAMPING = 0.5F;
   public static final float AL_ECHO_MIN_FEEDBACK = 0.0F;
   public static final float AL_ECHO_MAX_FEEDBACK = 1.0F;
   public static final float AL_ECHO_DEFAULT_FEEDBACK = 0.5F;
   public static final float AL_ECHO_MIN_SPREAD = -1.0F;
   public static final float AL_ECHO_MAX_SPREAD = 1.0F;
   public static final float AL_ECHO_DEFAULT_SPREAD = -1.0F;
   public static final int AL_FLANGER_WAVEFORM_SINUSOID = 0;
   public static final int AL_FLANGER_WAVEFORM_TRIANGLE = 1;
   public static final int AL_FLANGER_MIN_WAVEFORM = 0;
   public static final int AL_FLANGER_MAX_WAVEFORM = 1;
   public static final int AL_FLANGER_DEFAULT_WAVEFORM = 1;
   public static final int AL_FLANGER_MIN_PHASE = -180;
   public static final int AL_FLANGER_MAX_PHASE = 180;
   public static final int AL_FLANGER_DEFAULT_PHASE = 0;
   public static final float AL_FLANGER_MIN_RATE = 0.0F;
   public static final float AL_FLANGER_MAX_RATE = 10.0F;
   public static final float AL_FLANGER_DEFAULT_RATE = 0.27F;
   public static final float AL_FLANGER_MIN_DEPTH = 0.0F;
   public static final float AL_FLANGER_MAX_DEPTH = 1.0F;
   public static final float AL_FLANGER_DEFAULT_DEPTH = 1.0F;
   public static final float AL_FLANGER_MIN_FEEDBACK = -1.0F;
   public static final float AL_FLANGER_MAX_FEEDBACK = 1.0F;
   public static final float AL_FLANGER_DEFAULT_FEEDBACK = -0.5F;
   public static final float AL_FLANGER_MIN_DELAY = 0.0F;
   public static final float AL_FLANGER_MAX_DELAY = 0.004F;
   public static final float AL_FLANGER_DEFAULT_DELAY = 0.002F;
   public static final float AL_FREQUENCY_SHIFTER_MIN_FREQUENCY = 0.0F;
   public static final float AL_FREQUENCY_SHIFTER_MAX_FREQUENCY = 24000.0F;
   public static final float AL_FREQUENCY_SHIFTER_DEFAULT_FREQUENCY = 0.0F;
   public static final int AL_FREQUENCY_SHIFTER_MIN_LEFT_DIRECTION = 0;
   public static final int AL_FREQUENCY_SHIFTER_MAX_LEFT_DIRECTION = 2;
   public static final int AL_FREQUENCY_SHIFTER_DEFAULT_LEFT_DIRECTION = 0;
   public static final int AL_FREQUENCY_SHIFTER_DIRECTION_DOWN = 0;
   public static final int AL_FREQUENCY_SHIFTER_DIRECTION_UP = 1;
   public static final int AL_FREQUENCY_SHIFTER_DIRECTION_OFF = 2;
   public static final int AL_FREQUENCY_SHIFTER_MIN_RIGHT_DIRECTION = 0;
   public static final int AL_FREQUENCY_SHIFTER_MAX_RIGHT_DIRECTION = 2;
   public static final int AL_FREQUENCY_SHIFTER_DEFAULT_RIGHT_DIRECTION = 0;
   public static final int AL_VOCAL_MORPHER_MIN_PHONEMEA = 0;
   public static final int AL_VOCAL_MORPHER_MAX_PHONEMEA = 29;
   public static final int AL_VOCAL_MORPHER_DEFAULT_PHONEMEA = 0;
   public static final int AL_VOCAL_MORPHER_MIN_PHONEMEA_COARSE_TUNING = -24;
   public static final int AL_VOCAL_MORPHER_MAX_PHONEMEA_COARSE_TUNING = 24;
   public static final int AL_VOCAL_MORPHER_DEFAULT_PHONEMEA_COARSE_TUNING = 0;
   public static final int AL_VOCAL_MORPHER_MIN_PHONEMEB = 0;
   public static final int AL_VOCAL_MORPHER_MAX_PHONEMEB = 29;
   public static final int AL_VOCAL_MORPHER_DEFAULT_PHONEMEB = 10;
   public static final int AL_VOCAL_MORPHER_MIN_PHONEMEB_COARSE_TUNING = -24;
   public static final int AL_VOCAL_MORPHER_MAX_PHONEMEB_COARSE_TUNING = 24;
   public static final int AL_VOCAL_MORPHER_DEFAULT_PHONEMEB_COARSE_TUNING = 0;
   public static final int AL_VOCAL_MORPHER_PHONEME_A = 0;
   public static final int AL_VOCAL_MORPHER_PHONEME_E = 1;
   public static final int AL_VOCAL_MORPHER_PHONEME_I = 2;
   public static final int AL_VOCAL_MORPHER_PHONEME_O = 3;
   public static final int AL_VOCAL_MORPHER_PHONEME_U = 4;
   public static final int AL_VOCAL_MORPHER_PHONEME_AA = 5;
   public static final int AL_VOCAL_MORPHER_PHONEME_AE = 6;
   public static final int AL_VOCAL_MORPHER_PHONEME_AH = 7;
   public static final int AL_VOCAL_MORPHER_PHONEME_AO = 8;
   public static final int AL_VOCAL_MORPHER_PHONEME_EH = 9;
   public static final int AL_VOCAL_MORPHER_PHONEME_ER = 10;
   public static final int AL_VOCAL_MORPHER_PHONEME_IH = 11;
   public static final int AL_VOCAL_MORPHER_PHONEME_IY = 12;
   public static final int AL_VOCAL_MORPHER_PHONEME_UH = 13;
   public static final int AL_VOCAL_MORPHER_PHONEME_UW = 14;
   public static final int AL_VOCAL_MORPHER_PHONEME_B = 15;
   public static final int AL_VOCAL_MORPHER_PHONEME_D = 16;
   public static final int AL_VOCAL_MORPHER_PHONEME_F = 17;
   public static final int AL_VOCAL_MORPHER_PHONEME_G = 18;
   public static final int AL_VOCAL_MORPHER_PHONEME_J = 19;
   public static final int AL_VOCAL_MORPHER_PHONEME_K = 20;
   public static final int AL_VOCAL_MORPHER_PHONEME_L = 21;
   public static final int AL_VOCAL_MORPHER_PHONEME_M = 22;
   public static final int AL_VOCAL_MORPHER_PHONEME_N = 23;
   public static final int AL_VOCAL_MORPHER_PHONEME_P = 24;
   public static final int AL_VOCAL_MORPHER_PHONEME_R = 25;
   public static final int AL_VOCAL_MORPHER_PHONEME_S = 26;
   public static final int AL_VOCAL_MORPHER_PHONEME_T = 27;
   public static final int AL_VOCAL_MORPHER_PHONEME_V = 28;
   public static final int AL_VOCAL_MORPHER_PHONEME_Z = 29;
   public static final int AL_VOCAL_MORPHER_WAVEFORM_SINUSOID = 0;
   public static final int AL_VOCAL_MORPHER_WAVEFORM_TRIANGLE = 1;
   public static final int AL_VOCAL_MORPHER_WAVEFORM_SAWTOOTH = 2;
   public static final int AL_VOCAL_MORPHER_MIN_WAVEFORM = 0;
   public static final int AL_VOCAL_MORPHER_MAX_WAVEFORM = 2;
   public static final int AL_VOCAL_MORPHER_DEFAULT_WAVEFORM = 0;
   public static final float AL_VOCAL_MORPHER_MIN_RATE = 0.0F;
   public static final float AL_VOCAL_MORPHER_MAX_RATE = 10.0F;
   public static final float AL_VOCAL_MORPHER_DEFAULT_RATE = 1.41F;
   public static final int AL_PITCH_SHIFTER_MIN_COARSE_TUNE = -12;
   public static final int AL_PITCH_SHIFTER_MAX_COARSE_TUNE = 12;
   public static final int AL_PITCH_SHIFTER_DEFAULT_COARSE_TUNE = 12;
   public static final int AL_PITCH_SHIFTER_MIN_FINE_TUNE = -50;
   public static final int AL_PITCH_SHIFTER_MAX_FINE_TUNE = 50;
   public static final int AL_PITCH_SHIFTER_DEFAULT_FINE_TUNE = 0;
   public static final float AL_RING_MODULATOR_MIN_FREQUENCY = 0.0F;
   public static final float AL_RING_MODULATOR_MAX_FREQUENCY = 8000.0F;
   public static final float AL_RING_MODULATOR_DEFAULT_FREQUENCY = 440.0F;
   public static final float AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF = 0.0F;
   public static final float AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF = 24000.0F;
   public static final float AL_RING_MODULATOR_DEFAULT_HIGHPASS_CUTOFF = 800.0F;
   public static final int AL_RING_MODULATOR_SINUSOID = 0;
   public static final int AL_RING_MODULATOR_SAWTOOTH = 1;
   public static final int AL_RING_MODULATOR_SQUARE = 2;
   public static final int AL_RING_MODULATOR_MIN_WAVEFORM = 0;
   public static final int AL_RING_MODULATOR_MAX_WAVEFORM = 2;
   public static final int AL_RING_MODULATOR_DEFAULT_WAVEFORM = 0;
   public static final float AL_AUTOWAH_MIN_ATTACK_TIME = 1.0E-4F;
   public static final float AL_AUTOWAH_MAX_ATTACK_TIME = 1.0F;
   public static final float AL_AUTOWAH_DEFAULT_ATTACK_TIME = 0.06F;
   public static final float AL_AUTOWAH_MIN_RELEASE_TIME = 1.0E-4F;
   public static final float AL_AUTOWAH_MAX_RELEASE_TIME = 1.0F;
   public static final float AL_AUTOWAH_DEFAULT_RELEASE_TIME = 0.06F;
   public static final float AL_AUTOWAH_MIN_RESONANCE = 2.0F;
   public static final float AL_AUTOWAH_MAX_RESONANCE = 1000.0F;
   public static final float AL_AUTOWAH_DEFAULT_RESONANCE = 1000.0F;
   public static final float AL_AUTOWAH_MIN_PEAK_GAIN = 3.0E-5F;
   public static final float AL_AUTOWAH_MAX_PEAK_GAIN = 31621.0F;
   public static final float AL_AUTOWAH_DEFAULT_PEAK_GAIN = 11.22F;
   public static final int AL_COMPRESSOR_MIN_ONOFF = 0;
   public static final int AL_COMPRESSOR_MAX_ONOFF = 1;
   public static final int AL_COMPRESSOR_DEFAULT_ONOFF = 1;
   public static final float AL_EQUALIZER_MIN_LOW_GAIN = 0.126F;
   public static final float AL_EQUALIZER_MAX_LOW_GAIN = 7.943F;
   public static final float AL_EQUALIZER_DEFAULT_LOW_GAIN = 1.0F;
   public static final float AL_EQUALIZER_MIN_LOW_CUTOFF = 50.0F;
   public static final float AL_EQUALIZER_MAX_LOW_CUTOFF = 800.0F;
   public static final float AL_EQUALIZER_DEFAULT_LOW_CUTOFF = 200.0F;
   public static final float AL_EQUALIZER_MIN_MID1_GAIN = 0.126F;
   public static final float AL_EQUALIZER_MAX_MID1_GAIN = 7.943F;
   public static final float AL_EQUALIZER_DEFAULT_MID1_GAIN = 1.0F;
   public static final float AL_EQUALIZER_MIN_MID1_CENTER = 200.0F;
   public static final float AL_EQUALIZER_MAX_MID1_CENTER = 3000.0F;
   public static final float AL_EQUALIZER_DEFAULT_MID1_CENTER = 500.0F;
   public static final float AL_EQUALIZER_MIN_MID1_WIDTH = 0.01F;
   public static final float AL_EQUALIZER_MAX_MID1_WIDTH = 1.0F;
   public static final float AL_EQUALIZER_DEFAULT_MID1_WIDTH = 1.0F;
   public static final float AL_EQUALIZER_MIN_MID2_GAIN = 0.126F;
   public static final float AL_EQUALIZER_MAX_MID2_GAIN = 7.943F;
   public static final float AL_EQUALIZER_DEFAULT_MID2_GAIN = 1.0F;
   public static final float AL_EQUALIZER_MIN_MID2_CENTER = 1000.0F;
   public static final float AL_EQUALIZER_MAX_MID2_CENTER = 8000.0F;
   public static final float AL_EQUALIZER_DEFAULT_MID2_CENTER = 3000.0F;
   public static final float AL_EQUALIZER_MIN_MID2_WIDTH = 0.01F;
   public static final float AL_EQUALIZER_MAX_MID2_WIDTH = 1.0F;
   public static final float AL_EQUALIZER_DEFAULT_MID2_WIDTH = 1.0F;
   public static final float AL_EQUALIZER_MIN_HIGH_GAIN = 0.126F;
   public static final float AL_EQUALIZER_MAX_HIGH_GAIN = 7.943F;
   public static final float AL_EQUALIZER_DEFAULT_HIGH_GAIN = 1.0F;
   public static final float AL_EQUALIZER_MIN_HIGH_CUTOFF = 4000.0F;
   public static final float AL_EQUALIZER_MAX_HIGH_CUTOFF = 16000.0F;
   public static final float AL_EQUALIZER_DEFAULT_HIGH_CUTOFF = 6000.0F;
   public static final float LOWPASS_MIN_GAIN = 0.0F;
   public static final float LOWPASS_MAX_GAIN = 1.0F;
   public static final float LOWPASS_DEFAULT_GAIN = 1.0F;
   public static final float LOWPASS_MIN_GAINHF = 0.0F;
   public static final float LOWPASS_MAX_GAINHF = 1.0F;
   public static final float LOWPASS_DEFAULT_GAINHF = 1.0F;
   public static final float HIGHPASS_MIN_GAIN = 0.0F;
   public static final float HIGHPASS_MAX_GAIN = 1.0F;
   public static final float HIGHPASS_DEFAULT_GAIN = 1.0F;
   public static final float HIGHPASS_MIN_GAINLF = 0.0F;
   public static final float HIGHPASS_MAX_GAINLF = 1.0F;
   public static final float HIGHPASS_DEFAULT_GAINLF = 1.0F;
   public static final float BANDPASS_MIN_GAIN = 0.0F;
   public static final float BANDPASS_MAX_GAIN = 1.0F;
   public static final float BANDPASS_DEFAULT_GAIN = 1.0F;
   public static final float BANDPASS_MIN_GAINHF = 0.0F;
   public static final float BANDPASS_MAX_GAINHF = 1.0F;
   public static final float BANDPASS_DEFAULT_GAINHF = 1.0F;
   public static final float BANDPASS_MIN_GAINLF = 0.0F;
   public static final float BANDPASS_MAX_GAINLF = 1.0F;
   public static final float BANDPASS_DEFAULT_GAINLF = 1.0F;

   static native void initNativeStubs() throws LWJGLException;

   public static void alGenAuxiliaryEffectSlots(IntBuffer auxiliaryeffectslots) {
      BufferChecks.checkDirect(auxiliaryeffectslots);
      nalGenAuxiliaryEffectSlots(auxiliaryeffectslots.remaining(), MemoryUtil.getAddress(auxiliaryeffectslots));
   }

   static native void nalGenAuxiliaryEffectSlots(int var0, long var1);

   public static int alGenAuxiliaryEffectSlots() {
      int __result = nalGenAuxiliaryEffectSlots2(1);
      return __result;
   }

   static native int nalGenAuxiliaryEffectSlots2(int var0);

   public static void alDeleteAuxiliaryEffectSlots(IntBuffer auxiliaryeffectslots) {
      BufferChecks.checkDirect(auxiliaryeffectslots);
      nalDeleteAuxiliaryEffectSlots(auxiliaryeffectslots.remaining(), MemoryUtil.getAddress(auxiliaryeffectslots));
   }

   static native void nalDeleteAuxiliaryEffectSlots(int var0, long var1);

   public static void alDeleteAuxiliaryEffectSlots(int auxiliaryeffectslot) {
      nalDeleteAuxiliaryEffectSlots2(1, auxiliaryeffectslot);
   }

   static native void nalDeleteAuxiliaryEffectSlots2(int var0, int var1);

   public static boolean alIsAuxiliaryEffectSlot(int auxiliaryeffectslot) {
      boolean __result = nalIsAuxiliaryEffectSlot(auxiliaryeffectslot);
      return __result;
   }

   static native boolean nalIsAuxiliaryEffectSlot(int var0);

   public static void alAuxiliaryEffectSloti(int auxiliaryeffectslot, int param, int value) {
      nalAuxiliaryEffectSloti(auxiliaryeffectslot, param, value);
   }

   static native void nalAuxiliaryEffectSloti(int var0, int var1, int var2);

   public static void alAuxiliaryEffectSlot(int auxiliaryeffectslot, int param, IntBuffer values) {
      BufferChecks.checkBuffer((IntBuffer)values, 1);
      nalAuxiliaryEffectSlotiv(auxiliaryeffectslot, param, MemoryUtil.getAddress(values));
   }

   static native void nalAuxiliaryEffectSlotiv(int var0, int var1, long var2);

   public static void alAuxiliaryEffectSlotf(int auxiliaryeffectslot, int param, float value) {
      nalAuxiliaryEffectSlotf(auxiliaryeffectslot, param, value);
   }

   static native void nalAuxiliaryEffectSlotf(int var0, int var1, float var2);

   public static void alAuxiliaryEffectSlot(int auxiliaryeffectslot, int param, FloatBuffer values) {
      BufferChecks.checkBuffer((FloatBuffer)values, 1);
      nalAuxiliaryEffectSlotfv(auxiliaryeffectslot, param, MemoryUtil.getAddress(values));
   }

   static native void nalAuxiliaryEffectSlotfv(int var0, int var1, long var2);

   public static int alGetAuxiliaryEffectSloti(int auxiliaryeffectslot, int param) {
      int __result = nalGetAuxiliaryEffectSloti(auxiliaryeffectslot, param);
      return __result;
   }

   static native int nalGetAuxiliaryEffectSloti(int var0, int var1);

   public static void alGetAuxiliaryEffectSlot(int auxiliaryeffectslot, int param, IntBuffer intdata) {
      BufferChecks.checkBuffer((IntBuffer)intdata, 1);
      nalGetAuxiliaryEffectSlotiv(auxiliaryeffectslot, param, MemoryUtil.getAddress(intdata));
   }

   static native void nalGetAuxiliaryEffectSlotiv(int var0, int var1, long var2);

   public static float alGetAuxiliaryEffectSlotf(int auxiliaryeffectslot, int param) {
      float __result = nalGetAuxiliaryEffectSlotf(auxiliaryeffectslot, param);
      return __result;
   }

   static native float nalGetAuxiliaryEffectSlotf(int var0, int var1);

   public static void alGetAuxiliaryEffectSlot(int auxiliaryeffectslot, int param, FloatBuffer floatdata) {
      BufferChecks.checkBuffer((FloatBuffer)floatdata, 1);
      nalGetAuxiliaryEffectSlotfv(auxiliaryeffectslot, param, MemoryUtil.getAddress(floatdata));
   }

   static native void nalGetAuxiliaryEffectSlotfv(int var0, int var1, long var2);

   public static void alGenEffects(IntBuffer effects) {
      BufferChecks.checkDirect(effects);
      nalGenEffects(effects.remaining(), MemoryUtil.getAddress(effects));
   }

   static native void nalGenEffects(int var0, long var1);

   public static int alGenEffects() {
      int __result = nalGenEffects2(1);
      return __result;
   }

   static native int nalGenEffects2(int var0);

   public static void alDeleteEffects(IntBuffer effects) {
      BufferChecks.checkDirect(effects);
      nalDeleteEffects(effects.remaining(), MemoryUtil.getAddress(effects));
   }

   static native void nalDeleteEffects(int var0, long var1);

   public static void alDeleteEffects(int effect) {
      nalDeleteEffects2(1, effect);
   }

   static native void nalDeleteEffects2(int var0, int var1);

   public static boolean alIsEffect(int effect) {
      boolean __result = nalIsEffect(effect);
      return __result;
   }

   static native boolean nalIsEffect(int var0);

   public static void alEffecti(int effect, int param, int value) {
      nalEffecti(effect, param, value);
   }

   static native void nalEffecti(int var0, int var1, int var2);

   public static void alEffect(int effect, int param, IntBuffer values) {
      BufferChecks.checkBuffer((IntBuffer)values, 1);
      nalEffectiv(effect, param, MemoryUtil.getAddress(values));
   }

   static native void nalEffectiv(int var0, int var1, long var2);

   public static void alEffectf(int effect, int param, float value) {
      nalEffectf(effect, param, value);
   }

   static native void nalEffectf(int var0, int var1, float var2);

   public static void alEffect(int effect, int param, FloatBuffer values) {
      BufferChecks.checkBuffer((FloatBuffer)values, 1);
      nalEffectfv(effect, param, MemoryUtil.getAddress(values));
   }

   static native void nalEffectfv(int var0, int var1, long var2);

   public static int alGetEffecti(int effect, int param) {
      int __result = nalGetEffecti(effect, param);
      return __result;
   }

   static native int nalGetEffecti(int var0, int var1);

   public static void alGetEffect(int effect, int param, IntBuffer intdata) {
      BufferChecks.checkBuffer((IntBuffer)intdata, 1);
      nalGetEffectiv(effect, param, MemoryUtil.getAddress(intdata));
   }

   static native void nalGetEffectiv(int var0, int var1, long var2);

   public static float alGetEffectf(int effect, int param) {
      float __result = nalGetEffectf(effect, param);
      return __result;
   }

   static native float nalGetEffectf(int var0, int var1);

   public static void alGetEffect(int effect, int param, FloatBuffer floatdata) {
      BufferChecks.checkBuffer((FloatBuffer)floatdata, 1);
      nalGetEffectfv(effect, param, MemoryUtil.getAddress(floatdata));
   }

   static native void nalGetEffectfv(int var0, int var1, long var2);

   public static void alGenFilters(IntBuffer filters) {
      BufferChecks.checkDirect(filters);
      nalGenFilters(filters.remaining(), MemoryUtil.getAddress(filters));
   }

   static native void nalGenFilters(int var0, long var1);

   public static int alGenFilters() {
      int __result = nalGenFilters2(1);
      return __result;
   }

   static native int nalGenFilters2(int var0);

   public static void alDeleteFilters(IntBuffer filters) {
      BufferChecks.checkDirect(filters);
      nalDeleteFilters(filters.remaining(), MemoryUtil.getAddress(filters));
   }

   static native void nalDeleteFilters(int var0, long var1);

   public static void alDeleteFilters(int filter) {
      nalDeleteFilters2(1, filter);
   }

   static native void nalDeleteFilters2(int var0, int var1);

   public static boolean alIsFilter(int filter) {
      boolean __result = nalIsFilter(filter);
      return __result;
   }

   static native boolean nalIsFilter(int var0);

   public static void alFilteri(int filter, int param, int value) {
      nalFilteri(filter, param, value);
   }

   static native void nalFilteri(int var0, int var1, int var2);

   public static void alFilter(int filter, int param, IntBuffer values) {
      BufferChecks.checkBuffer((IntBuffer)values, 1);
      nalFilteriv(filter, param, MemoryUtil.getAddress(values));
   }

   static native void nalFilteriv(int var0, int var1, long var2);

   public static void alFilterf(int filter, int param, float value) {
      nalFilterf(filter, param, value);
   }

   static native void nalFilterf(int var0, int var1, float var2);

   public static void alFilter(int filter, int param, FloatBuffer values) {
      BufferChecks.checkBuffer((FloatBuffer)values, 1);
      nalFilterfv(filter, param, MemoryUtil.getAddress(values));
   }

   static native void nalFilterfv(int var0, int var1, long var2);

   public static int alGetFilteri(int filter, int param) {
      int __result = nalGetFilteri(filter, param);
      return __result;
   }

   static native int nalGetFilteri(int var0, int var1);

   public static void alGetFilter(int filter, int param, IntBuffer intdata) {
      BufferChecks.checkBuffer((IntBuffer)intdata, 1);
      nalGetFilteriv(filter, param, MemoryUtil.getAddress(intdata));
   }

   static native void nalGetFilteriv(int var0, int var1, long var2);

   public static float alGetFilterf(int filter, int param) {
      float __result = nalGetFilterf(filter, param);
      return __result;
   }

   static native float nalGetFilterf(int var0, int var1);

   public static void alGetFilter(int filter, int param, FloatBuffer floatdata) {
      BufferChecks.checkBuffer((FloatBuffer)floatdata, 1);
      nalGetFilterfv(filter, param, MemoryUtil.getAddress(floatdata));
   }

   static native void nalGetFilterfv(int var0, int var1, long var2);
}
