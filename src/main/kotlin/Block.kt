import java.io.File
import java.util.stream.Collectors
import kotlin.experimental.xor
import kotlin.system.exitProcess

private val SBox = arrayOf(
        byteArrayOf(0x63.toByte(), 0x7c.toByte(), 0x77.toByte(), 0x7b.toByte(), 0xf2.toByte(), 0x6b.toByte(), 0x6f.toByte(), 0xc5.toByte(), 0x30.toByte(), 0x01.toByte(), 0x67.toByte(), 0x2b.toByte(), 0xfe.toByte(), 0xd7.toByte(), 0xab.toByte(), 0x76.toByte()),
        byteArrayOf(0xca.toByte(), 0x82.toByte(), 0xc9.toByte(), 0x7d.toByte(), 0xfa.toByte(), 0x59.toByte(), 0x47.toByte(), 0xf0.toByte(), 0xad.toByte(), 0xd4.toByte(), 0xa2.toByte(), 0xaf.toByte(), 0x9c.toByte(), 0xa4.toByte(), 0x72.toByte(), 0xc0.toByte()),
        byteArrayOf(0xb7.toByte(), 0xfd.toByte(), 0x93.toByte(), 0x26.toByte(), 0x36.toByte(), 0x3f.toByte(), 0xf7.toByte(), 0xcc.toByte(), 0x34.toByte(), 0xa5.toByte(), 0xe5.toByte(), 0xf1.toByte(), 0x71.toByte(), 0xd8.toByte(), 0x31.toByte(), 0x15.toByte()),
        byteArrayOf(0x04.toByte(), 0xc7.toByte(), 0x23.toByte(), 0xc3.toByte(), 0x18.toByte(), 0x96.toByte(), 0x05.toByte(), 0x9a.toByte(), 0x07.toByte(), 0x12.toByte(), 0x80.toByte(), 0xe2.toByte(), 0xeb.toByte(), 0x27.toByte(), 0xb2.toByte(), 0x75.toByte()),
        byteArrayOf(0x09.toByte(), 0x83.toByte(), 0x2c.toByte(), 0x1a.toByte(), 0x1b.toByte(), 0x6e.toByte(), 0x5a.toByte(), 0xa0.toByte(), 0x52.toByte(), 0x3b.toByte(), 0xd6.toByte(), 0xb3.toByte(), 0x29.toByte(), 0xe3.toByte(), 0x2f.toByte(), 0x84.toByte()),
        byteArrayOf(0x53.toByte(), 0xd1.toByte(), 0x00.toByte(), 0xed.toByte(), 0x20.toByte(), 0xfc.toByte(), 0xb1.toByte(), 0x5b.toByte(), 0x6a.toByte(), 0xcb.toByte(), 0xbe.toByte(), 0x39.toByte(), 0x4a.toByte(), 0x4c.toByte(), 0x58.toByte(), 0xcf.toByte()),
        byteArrayOf(0xd0.toByte(), 0xef.toByte(), 0xaa.toByte(), 0xfb.toByte(), 0x43.toByte(), 0x4d.toByte(), 0x33.toByte(), 0x85.toByte(), 0x45.toByte(), 0xf9.toByte(), 0x02.toByte(), 0x7f.toByte(), 0x50.toByte(), 0x3c.toByte(), 0x9f.toByte(), 0xa8.toByte()),
        byteArrayOf(0x51.toByte(), 0xa3.toByte(), 0x40.toByte(), 0x8f.toByte(), 0x92.toByte(), 0x9d.toByte(), 0x38.toByte(), 0xf5.toByte(), 0xbc.toByte(), 0xb6.toByte(), 0xda.toByte(), 0x21.toByte(), 0x10.toByte(), 0xff.toByte(), 0xf3.toByte(), 0xd2.toByte()),
        byteArrayOf(0xcd.toByte(), 0x0c.toByte(), 0x13.toByte(), 0xec.toByte(), 0x5f.toByte(), 0x97.toByte(), 0x44.toByte(), 0x17.toByte(), 0xc4.toByte(), 0xa7.toByte(), 0x7e.toByte(), 0x3d.toByte(), 0x64.toByte(), 0x5d.toByte(), 0x19.toByte(), 0x73.toByte()),
        byteArrayOf(0x60.toByte(), 0x81.toByte(), 0x4f.toByte(), 0xdc.toByte(), 0x22.toByte(), 0x2a.toByte(), 0x90.toByte(), 0x88.toByte(), 0x46.toByte(), 0xee.toByte(), 0xb8.toByte(), 0x14.toByte(), 0xde.toByte(), 0x5e.toByte(), 0x0b.toByte(), 0xdb.toByte()),
        byteArrayOf(0xe0.toByte(), 0x32.toByte(), 0x3a.toByte(), 0x0a.toByte(), 0x49.toByte(), 0x06.toByte(), 0x24.toByte(), 0x5c.toByte(), 0xc2.toByte(), 0xd3.toByte(), 0xac.toByte(), 0x62.toByte(), 0x91.toByte(), 0x95.toByte(), 0xe4.toByte(), 0x79.toByte()),
        byteArrayOf(0xe7.toByte(), 0xc8.toByte(), 0x37.toByte(), 0x6d.toByte(), 0x8d.toByte(), 0xd5.toByte(), 0x4e.toByte(), 0xa9.toByte(), 0x6c.toByte(), 0x56.toByte(), 0xf4.toByte(), 0xea.toByte(), 0x65.toByte(), 0x7a.toByte(), 0xae.toByte(), 0x08.toByte()),
        byteArrayOf(0xba.toByte(), 0x78.toByte(), 0x25.toByte(), 0x2e.toByte(), 0x1c.toByte(), 0xa6.toByte(), 0xb4.toByte(), 0xc6.toByte(), 0xe8.toByte(), 0xdd.toByte(), 0x74.toByte(), 0x1f.toByte(), 0x4b.toByte(), 0xbd.toByte(), 0x8b.toByte(), 0x8a.toByte()),
        byteArrayOf(0x70.toByte(), 0x3e.toByte(), 0xb5.toByte(), 0x66.toByte(), 0x48.toByte(), 0x03.toByte(), 0xf6.toByte(), 0x0e.toByte(), 0x61.toByte(), 0x35.toByte(), 0x57.toByte(), 0xb9.toByte(), 0x86.toByte(), 0xc1.toByte(), 0x1d.toByte(), 0x9e.toByte()),
        byteArrayOf(0xe1.toByte(), 0xf8.toByte(), 0x98.toByte(), 0x11.toByte(), 0x69.toByte(), 0xd9.toByte(), 0x8e.toByte(), 0x94.toByte(), 0x9b.toByte(), 0x1e.toByte(), 0x87.toByte(), 0xe9.toByte(), 0xce.toByte(), 0x55.toByte(), 0x28.toByte(), 0xdf.toByte()),
        byteArrayOf(0x8c.toByte(), 0xa1.toByte(), 0x89.toByte(), 0x0d.toByte(), 0xbf.toByte(), 0xe6.toByte(), 0x42.toByte(), 0x68.toByte(), 0x41.toByte(), 0x99.toByte(), 0x2d.toByte(), 0x0f.toByte(), 0xb0.toByte(), 0x54.toByte(), 0xbb.toByte(), 0x16.toByte())
)

private val InvSBox = arrayOf(
        byteArrayOf(0x52.toByte(), 0x09.toByte(), 0x6a.toByte(), 0xd5.toByte(), 0x30.toByte(), 0x36.toByte(), 0xa5.toByte(), 0x38.toByte(), 0xbf.toByte(), 0x40.toByte(), 0xa3.toByte(), 0x9e.toByte(), 0x81.toByte(), 0xf3.toByte(), 0xd7.toByte(), 0xfb.toByte()),
        byteArrayOf(0x7c.toByte(), 0xe3.toByte(), 0x39.toByte(), 0x82.toByte(), 0x9b.toByte(), 0x2f.toByte(), 0xff.toByte(), 0x87.toByte(), 0x34.toByte(), 0x8e.toByte(), 0x43.toByte(), 0x44.toByte(), 0xc4.toByte(), 0xde.toByte(), 0xe9.toByte(), 0xcb.toByte()),
        byteArrayOf(0x54.toByte(), 0x7b.toByte(), 0x94.toByte(), 0x32.toByte(), 0xa6.toByte(), 0xc2.toByte(), 0x23.toByte(), 0x3d.toByte(), 0xee.toByte(), 0x4c.toByte(), 0x95.toByte(), 0x0b.toByte(), 0x42.toByte(), 0xfa.toByte(), 0xc3.toByte(), 0x4e.toByte()),
        byteArrayOf(0x08.toByte(), 0x2e.toByte(), 0xa1.toByte(), 0x66.toByte(), 0x28.toByte(), 0xd9.toByte(), 0x24.toByte(), 0xb2.toByte(), 0x76.toByte(), 0x5b.toByte(), 0xa2.toByte(), 0x49.toByte(), 0x6d.toByte(), 0x8b.toByte(), 0xd1.toByte(), 0x25.toByte()),
        byteArrayOf(0x72.toByte(), 0xf8.toByte(), 0xf6.toByte(), 0x64.toByte(), 0x86.toByte(), 0x68.toByte(), 0x98.toByte(), 0x16.toByte(), 0xd4.toByte(), 0xa4.toByte(), 0x5c.toByte(), 0xcc.toByte(), 0x5d.toByte(), 0x65.toByte(), 0xb6.toByte(), 0x92.toByte()),
        byteArrayOf(0x6c.toByte(), 0x70.toByte(), 0x48.toByte(), 0x50.toByte(), 0xfd.toByte(), 0xed.toByte(), 0xb9.toByte(), 0xda.toByte(), 0x5e.toByte(), 0x15.toByte(), 0x46.toByte(), 0x57.toByte(), 0xa7.toByte(), 0x8d.toByte(), 0x9d.toByte(), 0x84.toByte()),
        byteArrayOf(0x90.toByte(), 0xd8.toByte(), 0xab.toByte(), 0x00.toByte(), 0x8c.toByte(), 0xbc.toByte(), 0xd3.toByte(), 0x0a.toByte(), 0xf7.toByte(), 0xe4.toByte(), 0x58.toByte(), 0x05.toByte(), 0xb8.toByte(), 0xb3.toByte(), 0x45.toByte(), 0x06.toByte()),
        byteArrayOf(0xd0.toByte(), 0x2c.toByte(), 0x1e.toByte(), 0x8f.toByte(), 0xca.toByte(), 0x3f.toByte(), 0x0f.toByte(), 0x02.toByte(), 0xc1.toByte(), 0xaf.toByte(), 0xbd.toByte(), 0x03.toByte(), 0x01.toByte(), 0x13.toByte(), 0x8a.toByte(), 0x6b.toByte()),
        byteArrayOf(0x3a.toByte(), 0x91.toByte(), 0x11.toByte(), 0x41.toByte(), 0x4f.toByte(), 0x67.toByte(), 0xdc.toByte(), 0xea.toByte(), 0x97.toByte(), 0xf2.toByte(), 0xcf.toByte(), 0xce.toByte(), 0xf0.toByte(), 0xb4.toByte(), 0xe6.toByte(), 0x73.toByte()),
        byteArrayOf(0x96.toByte(), 0xac.toByte(), 0x74.toByte(), 0x22.toByte(), 0xe7.toByte(), 0xad.toByte(), 0x35.toByte(), 0x85.toByte(), 0xe2.toByte(), 0xf9.toByte(), 0x37.toByte(), 0xe8.toByte(), 0x1c.toByte(), 0x75.toByte(), 0xdf.toByte(), 0x6e.toByte()),
        byteArrayOf(0x47.toByte(), 0xf1.toByte(), 0x1a.toByte(), 0x71.toByte(), 0x1d.toByte(), 0x29.toByte(), 0xc5.toByte(), 0x89.toByte(), 0x6f.toByte(), 0xb7.toByte(), 0x62.toByte(), 0x0e.toByte(), 0xaa.toByte(), 0x18.toByte(), 0xbe.toByte(), 0x1b.toByte()),
        byteArrayOf(0xfc.toByte(), 0x56.toByte(), 0x3e.toByte(), 0x4b.toByte(), 0xc6.toByte(), 0xd2.toByte(), 0x79.toByte(), 0x20.toByte(), 0x9a.toByte(), 0xdb.toByte(), 0xc0.toByte(), 0xfe.toByte(), 0x78.toByte(), 0xcd.toByte(), 0x5a.toByte(), 0xf4.toByte()),
        byteArrayOf(0x1f.toByte(), 0xdd.toByte(), 0xa8.toByte(), 0x33.toByte(), 0x88.toByte(), 0x07.toByte(), 0xc7.toByte(), 0x31.toByte(), 0xb1.toByte(), 0x12.toByte(), 0x10.toByte(), 0x59.toByte(), 0x27.toByte(), 0x80.toByte(), 0xec.toByte(), 0x5f.toByte()),
        byteArrayOf(0x60.toByte(), 0x51.toByte(), 0x7f.toByte(), 0xa9.toByte(), 0x19.toByte(), 0xb5.toByte(), 0x4a.toByte(), 0x0d.toByte(), 0x2d.toByte(), 0xe5.toByte(), 0x7a.toByte(), 0x9f.toByte(), 0x93.toByte(), 0xc9.toByte(), 0x9c.toByte(), 0xef.toByte()),
        byteArrayOf(0xa0.toByte(), 0xe0.toByte(), 0x3b.toByte(), 0x4d.toByte(), 0xae.toByte(), 0x2a.toByte(), 0xf5.toByte(), 0xb0.toByte(), 0xc8.toByte(), 0xeb.toByte(), 0xbb.toByte(), 0x3c.toByte(), 0x83.toByte(), 0x53.toByte(), 0x99.toByte(), 0x61.toByte()),
        byteArrayOf(0x17.toByte(), 0x2b.toByte(), 0x04.toByte(), 0x7e.toByte(), 0xba.toByte(), 0x77.toByte(), 0xd6.toByte(), 0x26.toByte(), 0xe1.toByte(), 0x69.toByte(), 0x14.toByte(), 0x63.toByte(), 0x55.toByte(), 0x21.toByte(), 0x0c.toByte(), 0x7d.toByte())
)

private val m2 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x02.toByte(), 0x04.toByte(), 0x06.toByte(), 0x08.toByte(), 0x0a.toByte(), 0x0c.toByte(), 0x0e.toByte(), 0x10.toByte(), 0x12.toByte(), 0x14.toByte(), 0x16.toByte(), 0x18.toByte(), 0x1a.toByte(), 0x1c.toByte(), 0x1e.toByte()),
        byteArrayOf(0x20.toByte(), 0x22.toByte(), 0x24.toByte(), 0x26.toByte(), 0x28.toByte(), 0x2a.toByte(), 0x2c.toByte(), 0x2e.toByte(), 0x30.toByte(), 0x32.toByte(), 0x34.toByte(), 0x36.toByte(), 0x38.toByte(), 0x3a.toByte(), 0x3c.toByte(), 0x3e.toByte()),
        byteArrayOf(0x40.toByte(), 0x42.toByte(), 0x44.toByte(), 0x46.toByte(), 0x48.toByte(), 0x4a.toByte(), 0x4c.toByte(), 0x4e.toByte(), 0x50.toByte(), 0x52.toByte(), 0x54.toByte(), 0x56.toByte(), 0x58.toByte(), 0x5a.toByte(), 0x5c.toByte(), 0x5e.toByte()),
        byteArrayOf(0x60.toByte(), 0x62.toByte(), 0x64.toByte(), 0x66.toByte(), 0x68.toByte(), 0x6a.toByte(), 0x6c.toByte(), 0x6e.toByte(), 0x70.toByte(), 0x72.toByte(), 0x74.toByte(), 0x76.toByte(), 0x78.toByte(), 0x7a.toByte(), 0x7c.toByte(), 0x7e.toByte()),
        byteArrayOf(0x80.toByte(), 0x82.toByte(), 0x84.toByte(), 0x86.toByte(), 0x88.toByte(), 0x8a.toByte(), 0x8c.toByte(), 0x8e.toByte(), 0x90.toByte(), 0x92.toByte(), 0x94.toByte(), 0x96.toByte(), 0x98.toByte(), 0x9a.toByte(), 0x9c.toByte(), 0x9e.toByte()),
        byteArrayOf(0xa0.toByte(), 0xa2.toByte(), 0xa4.toByte(), 0xa6.toByte(), 0xa8.toByte(), 0xaa.toByte(), 0xac.toByte(), 0xae.toByte(), 0xb0.toByte(), 0xb2.toByte(), 0xb4.toByte(), 0xb6.toByte(), 0xb8.toByte(), 0xba.toByte(), 0xbc.toByte(), 0xbe.toByte()),
        byteArrayOf(0xc0.toByte(), 0xc2.toByte(), 0xc4.toByte(), 0xc6.toByte(), 0xc8.toByte(), 0xca.toByte(), 0xcc.toByte(), 0xce.toByte(), 0xd0.toByte(), 0xd2.toByte(), 0xd4.toByte(), 0xd6.toByte(), 0xd8.toByte(), 0xda.toByte(), 0xdc.toByte(), 0xde.toByte()),
        byteArrayOf(0xe0.toByte(), 0xe2.toByte(), 0xe4.toByte(), 0xe6.toByte(), 0xe8.toByte(), 0xea.toByte(), 0xec.toByte(), 0xee.toByte(), 0xf0.toByte(), 0xf2.toByte(), 0xf4.toByte(), 0xf6.toByte(), 0xf8.toByte(), 0xfa.toByte(), 0xfc.toByte(), 0xfe.toByte()),
        byteArrayOf(0x1b.toByte(), 0x19.toByte(), 0x1f.toByte(), 0x1d.toByte(), 0x13.toByte(), 0x11.toByte(), 0x17.toByte(), 0x15.toByte(), 0x0b.toByte(), 0x09.toByte(), 0x0f.toByte(), 0x0d.toByte(), 0x03.toByte(), 0x01.toByte(), 0x07.toByte(), 0x05.toByte()),
        byteArrayOf(0x3b.toByte(), 0x39.toByte(), 0x3f.toByte(), 0x3d.toByte(), 0x33.toByte(), 0x31.toByte(), 0x37.toByte(), 0x35.toByte(), 0x2b.toByte(), 0x29.toByte(), 0x2f.toByte(), 0x2d.toByte(), 0x23.toByte(), 0x21.toByte(), 0x27.toByte(), 0x25.toByte()),
        byteArrayOf(0x5b.toByte(), 0x59.toByte(), 0x5f.toByte(), 0x5d.toByte(), 0x53.toByte(), 0x51.toByte(), 0x57.toByte(), 0x55.toByte(), 0x4b.toByte(), 0x49.toByte(), 0x4f.toByte(), 0x4d.toByte(), 0x43.toByte(), 0x41.toByte(), 0x47.toByte(), 0x45.toByte()),
        byteArrayOf(0x7b.toByte(), 0x79.toByte(), 0x7f.toByte(), 0x7d.toByte(), 0x73.toByte(), 0x71.toByte(), 0x77.toByte(), 0x75.toByte(), 0x6b.toByte(), 0x69.toByte(), 0x6f.toByte(), 0x6d.toByte(), 0x63.toByte(), 0x61.toByte(), 0x67.toByte(), 0x65.toByte()),
        byteArrayOf(0x9b.toByte(), 0x99.toByte(), 0x9f.toByte(), 0x9d.toByte(), 0x93.toByte(), 0x91.toByte(), 0x97.toByte(), 0x95.toByte(), 0x8b.toByte(), 0x89.toByte(), 0x8f.toByte(), 0x8d.toByte(), 0x83.toByte(), 0x81.toByte(), 0x87.toByte(), 0x85.toByte()),
        byteArrayOf(0xbb.toByte(), 0xb9.toByte(), 0xbf.toByte(), 0xbd.toByte(), 0xb3.toByte(), 0xb1.toByte(), 0xb7.toByte(), 0xb5.toByte(), 0xab.toByte(), 0xa9.toByte(), 0xaf.toByte(), 0xad.toByte(), 0xa3.toByte(), 0xa1.toByte(), 0xa7.toByte(), 0xa5.toByte()),
        byteArrayOf(0xdb.toByte(), 0xd9.toByte(), 0xdf.toByte(), 0xdd.toByte(), 0xd3.toByte(), 0xd1.toByte(), 0xd7.toByte(), 0xd5.toByte(), 0xcb.toByte(), 0xc9.toByte(), 0xcf.toByte(), 0xcd.toByte(), 0xc3.toByte(), 0xc1.toByte(), 0xc7.toByte(), 0xc5.toByte()),
        byteArrayOf(0xfb.toByte(), 0xf9.toByte(), 0xff.toByte(), 0xfd.toByte(), 0xf3.toByte(), 0xf1.toByte(), 0xf7.toByte(), 0xf5.toByte(), 0xeb.toByte(), 0xe9.toByte(), 0xef.toByte(), 0xed.toByte(), 0xe3.toByte(), 0xe1.toByte(), 0xe7.toByte(), 0xe5.toByte())
)

private val m3 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x03.toByte(), 0x06.toByte(), 0x05.toByte(), 0x0c.toByte(), 0x0f.toByte(), 0x0a.toByte(), 0x09.toByte(), 0x18.toByte(), 0x1b.toByte(), 0x1e.toByte(), 0x1d.toByte(), 0x14.toByte(), 0x17.toByte(), 0x12.toByte(), 0x11.toByte()),
        byteArrayOf(0x30.toByte(), 0x33.toByte(), 0x36.toByte(), 0x35.toByte(), 0x3c.toByte(), 0x3f.toByte(), 0x3a.toByte(), 0x39.toByte(), 0x28.toByte(), 0x2b.toByte(), 0x2e.toByte(), 0x2d.toByte(), 0x24.toByte(), 0x27.toByte(), 0x22.toByte(), 0x21.toByte()),
        byteArrayOf(0x60.toByte(), 0x63.toByte(), 0x66.toByte(), 0x65.toByte(), 0x6c.toByte(), 0x6f.toByte(), 0x6a.toByte(), 0x69.toByte(), 0x78.toByte(), 0x7b.toByte(), 0x7e.toByte(), 0x7d.toByte(), 0x74.toByte(), 0x77.toByte(), 0x72.toByte(), 0x71.toByte()),
        byteArrayOf(0x50.toByte(), 0x53.toByte(), 0x56.toByte(), 0x55.toByte(), 0x5c.toByte(), 0x5f.toByte(), 0x5a.toByte(), 0x59.toByte(), 0x48.toByte(), 0x4b.toByte(), 0x4e.toByte(), 0x4d.toByte(), 0x44.toByte(), 0x47.toByte(), 0x42.toByte(), 0x41.toByte()),
        byteArrayOf(0xc0.toByte(), 0xc3.toByte(), 0xc6.toByte(), 0xc5.toByte(), 0xcc.toByte(), 0xcf.toByte(), 0xca.toByte(), 0xc9.toByte(), 0xd8.toByte(), 0xdb.toByte(), 0xde.toByte(), 0xdd.toByte(), 0xd4.toByte(), 0xd7.toByte(), 0xd2.toByte(), 0xd1.toByte()),
        byteArrayOf(0xf0.toByte(), 0xf3.toByte(), 0xf6.toByte(), 0xf5.toByte(), 0xfc.toByte(), 0xff.toByte(), 0xfa.toByte(), 0xf9.toByte(), 0xe8.toByte(), 0xeb.toByte(), 0xee.toByte(), 0xed.toByte(), 0xe4.toByte(), 0xe7.toByte(), 0xe2.toByte(), 0xe1.toByte()),
        byteArrayOf(0xa0.toByte(), 0xa3.toByte(), 0xa6.toByte(), 0xa5.toByte(), 0xac.toByte(), 0xaf.toByte(), 0xaa.toByte(), 0xa9.toByte(), 0xb8.toByte(), 0xbb.toByte(), 0xbe.toByte(), 0xbd.toByte(), 0xb4.toByte(), 0xb7.toByte(), 0xb2.toByte(), 0xb1.toByte()),
        byteArrayOf(0x90.toByte(), 0x93.toByte(), 0x96.toByte(), 0x95.toByte(), 0x9c.toByte(), 0x9f.toByte(), 0x9a.toByte(), 0x99.toByte(), 0x88.toByte(), 0x8b.toByte(), 0x8e.toByte(), 0x8d.toByte(), 0x84.toByte(), 0x87.toByte(), 0x82.toByte(), 0x81.toByte()),
        byteArrayOf(0x9b.toByte(), 0x98.toByte(), 0x9d.toByte(), 0x9e.toByte(), 0x97.toByte(), 0x94.toByte(), 0x91.toByte(), 0x92.toByte(), 0x83.toByte(), 0x80.toByte(), 0x85.toByte(), 0x86.toByte(), 0x8f.toByte(), 0x8c.toByte(), 0x89.toByte(), 0x8a.toByte()),
        byteArrayOf(0xab.toByte(), 0xa8.toByte(), 0xad.toByte(), 0xae.toByte(), 0xa7.toByte(), 0xa4.toByte(), 0xa1.toByte(), 0xa2.toByte(), 0xb3.toByte(), 0xb0.toByte(), 0xb5.toByte(), 0xb6.toByte(), 0xbf.toByte(), 0xbc.toByte(), 0xb9.toByte(), 0xba.toByte()),
        byteArrayOf(0xfb.toByte(), 0xf8.toByte(), 0xfd.toByte(), 0xfe.toByte(), 0xf7.toByte(), 0xf4.toByte(), 0xf1.toByte(), 0xf2.toByte(), 0xe3.toByte(), 0xe0.toByte(), 0xe5.toByte(), 0xe6.toByte(), 0xef.toByte(), 0xec.toByte(), 0xe9.toByte(), 0xea.toByte()),
        byteArrayOf(0xcb.toByte(), 0xc8.toByte(), 0xcd.toByte(), 0xce.toByte(), 0xc7.toByte(), 0xc4.toByte(), 0xc1.toByte(), 0xc2.toByte(), 0xd3.toByte(), 0xd0.toByte(), 0xd5.toByte(), 0xd6.toByte(), 0xdf.toByte(), 0xdc.toByte(), 0xd9.toByte(), 0xda.toByte()),
        byteArrayOf(0x5b.toByte(), 0x58.toByte(), 0x5d.toByte(), 0x5e.toByte(), 0x57.toByte(), 0x54.toByte(), 0x51.toByte(), 0x52.toByte(), 0x43.toByte(), 0x40.toByte(), 0x45.toByte(), 0x46.toByte(), 0x4f.toByte(), 0x4c.toByte(), 0x49.toByte(), 0x4a.toByte()),
        byteArrayOf(0x6b.toByte(), 0x68.toByte(), 0x6d.toByte(), 0x6e.toByte(), 0x67.toByte(), 0x64.toByte(), 0x61.toByte(), 0x62.toByte(), 0x73.toByte(), 0x70.toByte(), 0x75.toByte(), 0x76.toByte(), 0x7f.toByte(), 0x7c.toByte(), 0x79.toByte(), 0x7a.toByte()),
        byteArrayOf(0x3b.toByte(), 0x38.toByte(), 0x3d.toByte(), 0x3e.toByte(), 0x37.toByte(), 0x34.toByte(), 0x31.toByte(), 0x32.toByte(), 0x23.toByte(), 0x20.toByte(), 0x25.toByte(), 0x26.toByte(), 0x2f.toByte(), 0x2c.toByte(), 0x29.toByte(), 0x2a.toByte()),
        byteArrayOf(0x0b.toByte(), 0x08.toByte(), 0x0d.toByte(), 0x0e.toByte(), 0x07.toByte(), 0x04.toByte(), 0x01.toByte(), 0x02.toByte(), 0x13.toByte(), 0x10.toByte(), 0x15.toByte(), 0x16.toByte(), 0x1f.toByte(), 0x1c.toByte(), 0x19.toByte(), 0x1a.toByte())
)

private val m9 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x09.toByte(), 0x12.toByte(), 0x1b.toByte(), 0x24.toByte(), 0x2d.toByte(), 0x36.toByte(), 0x3f.toByte(), 0x48.toByte(), 0x41.toByte(), 0x5a.toByte(), 0x53.toByte(), 0x6c.toByte(), 0x65.toByte(), 0x7e.toByte(), 0x77.toByte()),
        byteArrayOf(0x90.toByte(), 0x99.toByte(), 0x82.toByte(), 0x8b.toByte(), 0xb4.toByte(), 0xbd.toByte(), 0xa6.toByte(), 0xaf.toByte(), 0xd8.toByte(), 0xd1.toByte(), 0xca.toByte(), 0xc3.toByte(), 0xfc.toByte(), 0xf5.toByte(), 0xee.toByte(), 0xe7.toByte()),
        byteArrayOf(0x3b.toByte(), 0x32.toByte(), 0x29.toByte(), 0x20.toByte(), 0x1f.toByte(), 0x16.toByte(), 0x0d.toByte(), 0x04.toByte(), 0x73.toByte(), 0x7a.toByte(), 0x61.toByte(), 0x68.toByte(), 0x57.toByte(), 0x5e.toByte(), 0x45.toByte(), 0x4c.toByte()),
        byteArrayOf(0xab.toByte(), 0xa2.toByte(), 0xb9.toByte(), 0xb0.toByte(), 0x8f.toByte(), 0x86.toByte(), 0x9d.toByte(), 0x94.toByte(), 0xe3.toByte(), 0xea.toByte(), 0xf1.toByte(), 0xf8.toByte(), 0xc7.toByte(), 0xce.toByte(), 0xd5.toByte(), 0xdc.toByte()),
        byteArrayOf(0x76.toByte(), 0x7f.toByte(), 0x64.toByte(), 0x6d.toByte(), 0x52.toByte(), 0x5b.toByte(), 0x40.toByte(), 0x49.toByte(), 0x3e.toByte(), 0x37.toByte(), 0x2c.toByte(), 0x25.toByte(), 0x1a.toByte(), 0x13.toByte(), 0x08.toByte(), 0x01.toByte()),
        byteArrayOf(0xe6.toByte(), 0xef.toByte(), 0xf4.toByte(), 0xfd.toByte(), 0xc2.toByte(), 0xcb.toByte(), 0xd0.toByte(), 0xd9.toByte(), 0xae.toByte(), 0xa7.toByte(), 0xbc.toByte(), 0xb5.toByte(), 0x8a.toByte(), 0x83.toByte(), 0x98.toByte(), 0x91.toByte()),
        byteArrayOf(0x4d.toByte(), 0x44.toByte(), 0x5f.toByte(), 0x56.toByte(), 0x69.toByte(), 0x60.toByte(), 0x7b.toByte(), 0x72.toByte(), 0x05.toByte(), 0x0c.toByte(), 0x17.toByte(), 0x1e.toByte(), 0x21.toByte(), 0x28.toByte(), 0x33.toByte(), 0x3a.toByte()),
        byteArrayOf(0xdd.toByte(), 0xd4.toByte(), 0xcf.toByte(), 0xc6.toByte(), 0xf9.toByte(), 0xf0.toByte(), 0xeb.toByte(), 0xe2.toByte(), 0x95.toByte(), 0x9c.toByte(), 0x87.toByte(), 0x8e.toByte(), 0xb1.toByte(), 0xb8.toByte(), 0xa3.toByte(), 0xaa.toByte()),
        byteArrayOf(0xec.toByte(), 0xe5.toByte(), 0xfe.toByte(), 0xf7.toByte(), 0xc8.toByte(), 0xc1.toByte(), 0xda.toByte(), 0xd3.toByte(), 0xa4.toByte(), 0xad.toByte(), 0xb6.toByte(), 0xbf.toByte(), 0x80.toByte(), 0x89.toByte(), 0x92.toByte(), 0x9b.toByte()),
        byteArrayOf(0x7c.toByte(), 0x75.toByte(), 0x6e.toByte(), 0x67.toByte(), 0x58.toByte(), 0x51.toByte(), 0x4a.toByte(), 0x43.toByte(), 0x34.toByte(), 0x3d.toByte(), 0x26.toByte(), 0x2f.toByte(), 0x10.toByte(), 0x19.toByte(), 0x02.toByte(), 0x0b.toByte()),
        byteArrayOf(0xd7.toByte(), 0xde.toByte(), 0xc5.toByte(), 0xcc.toByte(), 0xf3.toByte(), 0xfa.toByte(), 0xe1.toByte(), 0xe8.toByte(), 0x9f.toByte(), 0x96.toByte(), 0x8d.toByte(), 0x84.toByte(), 0xbb.toByte(), 0xb2.toByte(), 0xa9.toByte(), 0xa0.toByte()),
        byteArrayOf(0x47.toByte(), 0x4e.toByte(), 0x55.toByte(), 0x5c.toByte(), 0x63.toByte(), 0x6a.toByte(), 0x71.toByte(), 0x78.toByte(), 0x0f.toByte(), 0x06.toByte(), 0x1d.toByte(), 0x14.toByte(), 0x2b.toByte(), 0x22.toByte(), 0x39.toByte(), 0x30.toByte()),
        byteArrayOf(0x9a.toByte(), 0x93.toByte(), 0x88.toByte(), 0x81.toByte(), 0xbe.toByte(), 0xb7.toByte(), 0xac.toByte(), 0xa5.toByte(), 0xd2.toByte(), 0xdb.toByte(), 0xc0.toByte(), 0xc9.toByte(), 0xf6.toByte(), 0xff.toByte(), 0xe4.toByte(), 0xed.toByte()),
        byteArrayOf(0x0a.toByte(), 0x03.toByte(), 0x18.toByte(), 0x11.toByte(), 0x2e.toByte(), 0x27.toByte(), 0x3c.toByte(), 0x35.toByte(), 0x42.toByte(), 0x4b.toByte(), 0x50.toByte(), 0x59.toByte(), 0x66.toByte(), 0x6f.toByte(), 0x74.toByte(), 0x7d.toByte()),
        byteArrayOf(0xa1.toByte(), 0xa8.toByte(), 0xb3.toByte(), 0xba.toByte(), 0x85.toByte(), 0x8c.toByte(), 0x97.toByte(), 0x9e.toByte(), 0xe9.toByte(), 0xe0.toByte(), 0xfb.toByte(), 0xf2.toByte(), 0xcd.toByte(), 0xc4.toByte(), 0xdf.toByte(), 0xd6.toByte()),
        byteArrayOf(0x31.toByte(), 0x38.toByte(), 0x23.toByte(), 0x2a.toByte(), 0x15.toByte(), 0x1c.toByte(), 0x07.toByte(), 0x0e.toByte(), 0x79.toByte(), 0x70.toByte(), 0x6b.toByte(), 0x62.toByte(), 0x5d.toByte(), 0x54.toByte(), 0x4f.toByte(), 0x46.toByte())
)

private val m11 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x0b.toByte(), 0x16.toByte(), 0x1d.toByte(), 0x2c.toByte(), 0x27.toByte(), 0x3a.toByte(), 0x31.toByte(), 0x58.toByte(), 0x53.toByte(), 0x4e.toByte(), 0x45.toByte(), 0x74.toByte(), 0x7f.toByte(), 0x62.toByte(), 0x69.toByte()),
        byteArrayOf(0xb0.toByte(), 0xbb.toByte(), 0xa6.toByte(), 0xad.toByte(), 0x9c.toByte(), 0x97.toByte(), 0x8a.toByte(), 0x81.toByte(), 0xe8.toByte(), 0xe3.toByte(), 0xfe.toByte(), 0xf5.toByte(), 0xc4.toByte(), 0xcf.toByte(), 0xd2.toByte(), 0xd9.toByte()),
        byteArrayOf(0x7b.toByte(), 0x70.toByte(), 0x6d.toByte(), 0x66.toByte(), 0x57.toByte(), 0x5c.toByte(), 0x41.toByte(), 0x4a.toByte(), 0x23.toByte(), 0x28.toByte(), 0x35.toByte(), 0x3e.toByte(), 0x0f.toByte(), 0x04.toByte(), 0x19.toByte(), 0x12.toByte()),
        byteArrayOf(0xcb.toByte(), 0xc0.toByte(), 0xdd.toByte(), 0xd6.toByte(), 0xe7.toByte(), 0xec.toByte(), 0xf1.toByte(), 0xfa.toByte(), 0x93.toByte(), 0x98.toByte(), 0x85.toByte(), 0x8e.toByte(), 0xbf.toByte(), 0xb4.toByte(), 0xa9.toByte(), 0xa2.toByte()),
        byteArrayOf(0xf6.toByte(), 0xfd.toByte(), 0xe0.toByte(), 0xeb.toByte(), 0xda.toByte(), 0xd1.toByte(), 0xcc.toByte(), 0xc7.toByte(), 0xae.toByte(), 0xa5.toByte(), 0xb8.toByte(), 0xb3.toByte(), 0x82.toByte(), 0x89.toByte(), 0x94.toByte(), 0x9f.toByte()),
        byteArrayOf(0x46.toByte(), 0x4d.toByte(), 0x50.toByte(), 0x5b.toByte(), 0x6a.toByte(), 0x61.toByte(), 0x7c.toByte(), 0x77.toByte(), 0x1e.toByte(), 0x15.toByte(), 0x08.toByte(), 0x03.toByte(), 0x32.toByte(), 0x39.toByte(), 0x24.toByte(), 0x2f.toByte()),
        byteArrayOf(0x8d.toByte(), 0x86.toByte(), 0x9b.toByte(), 0x90.toByte(), 0xa1.toByte(), 0xaa.toByte(), 0xb7.toByte(), 0xbc.toByte(), 0xd5.toByte(), 0xde.toByte(), 0xc3.toByte(), 0xc8.toByte(), 0xf9.toByte(), 0xf2.toByte(), 0xef.toByte(), 0xe4.toByte()),
        byteArrayOf(0x3d.toByte(), 0x36.toByte(), 0x2b.toByte(), 0x20.toByte(), 0x11.toByte(), 0x1a.toByte(), 0x07.toByte(), 0x0c.toByte(), 0x65.toByte(), 0x6e.toByte(), 0x73.toByte(), 0x78.toByte(), 0x49.toByte(), 0x42.toByte(), 0x5f.toByte(), 0x54.toByte()),
        byteArrayOf(0xf7.toByte(), 0xfc.toByte(), 0xe1.toByte(), 0xea.toByte(), 0xdb.toByte(), 0xd0.toByte(), 0xcd.toByte(), 0xc6.toByte(), 0xaf.toByte(), 0xa4.toByte(), 0xb9.toByte(), 0xb2.toByte(), 0x83.toByte(), 0x88.toByte(), 0x95.toByte(), 0x9e.toByte()),
        byteArrayOf(0x47.toByte(), 0x4c.toByte(), 0x51.toByte(), 0x5a.toByte(), 0x6b.toByte(), 0x60.toByte(), 0x7d.toByte(), 0x76.toByte(), 0x1f.toByte(), 0x14.toByte(), 0x09.toByte(), 0x02.toByte(), 0x33.toByte(), 0x38.toByte(), 0x25.toByte(), 0x2e.toByte()),
        byteArrayOf(0x8c.toByte(), 0x87.toByte(), 0x9a.toByte(), 0x91.toByte(), 0xa0.toByte(), 0xab.toByte(), 0xb6.toByte(), 0xbd.toByte(), 0xd4.toByte(), 0xdf.toByte(), 0xc2.toByte(), 0xc9.toByte(), 0xf8.toByte(), 0xf3.toByte(), 0xee.toByte(), 0xe5.toByte()),
        byteArrayOf(0x3c.toByte(), 0x37.toByte(), 0x2a.toByte(), 0x21.toByte(), 0x10.toByte(), 0x1b.toByte(), 0x06.toByte(), 0x0d.toByte(), 0x64.toByte(), 0x6f.toByte(), 0x72.toByte(), 0x79.toByte(), 0x48.toByte(), 0x43.toByte(), 0x5e.toByte(), 0x55.toByte()),
        byteArrayOf(0x01.toByte(), 0x0a.toByte(), 0x17.toByte(), 0x1c.toByte(), 0x2d.toByte(), 0x26.toByte(), 0x3b.toByte(), 0x30.toByte(), 0x59.toByte(), 0x52.toByte(), 0x4f.toByte(), 0x44.toByte(), 0x75.toByte(), 0x7e.toByte(), 0x63.toByte(), 0x68.toByte()),
        byteArrayOf(0xb1.toByte(), 0xba.toByte(), 0xa7.toByte(), 0xac.toByte(), 0x9d.toByte(), 0x96.toByte(), 0x8b.toByte(), 0x80.toByte(), 0xe9.toByte(), 0xe2.toByte(), 0xff.toByte(), 0xf4.toByte(), 0xc5.toByte(), 0xce.toByte(), 0xd3.toByte(), 0xd8.toByte()),
        byteArrayOf(0x7a.toByte(), 0x71.toByte(), 0x6c.toByte(), 0x67.toByte(), 0x56.toByte(), 0x5d.toByte(), 0x40.toByte(), 0x4b.toByte(), 0x22.toByte(), 0x29.toByte(), 0x34.toByte(), 0x3f.toByte(), 0x0e.toByte(), 0x05.toByte(), 0x18.toByte(), 0x13.toByte()),
        byteArrayOf(0xca.toByte(), 0xc1.toByte(), 0xdc.toByte(), 0xd7.toByte(), 0xe6.toByte(), 0xed.toByte(), 0xf0.toByte(), 0xfb.toByte(), 0x92.toByte(), 0x99.toByte(), 0x84.toByte(), 0x8f.toByte(), 0xbe.toByte(), 0xb5.toByte(), 0xa8.toByte(), 0xa3.toByte()),
)

private val m13 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x0d.toByte(), 0x1a.toByte(), 0x17.toByte(), 0x34.toByte(), 0x39.toByte(), 0x2e.toByte(), 0x23.toByte(), 0x68.toByte(), 0x65.toByte(), 0x72.toByte(), 0x7f.toByte(), 0x5c.toByte(), 0x51.toByte(), 0x46.toByte(), 0x4b.toByte()),
        byteArrayOf(0xd0.toByte(), 0xdd.toByte(), 0xca.toByte(), 0xc7.toByte(), 0xe4.toByte(), 0xe9.toByte(), 0xfe.toByte(), 0xf3.toByte(), 0xb8.toByte(), 0xb5.toByte(), 0xa2.toByte(), 0xaf.toByte(), 0x8c.toByte(), 0x81.toByte(), 0x96.toByte(), 0x9b.toByte()),
        byteArrayOf(0xbb.toByte(), 0xb6.toByte(), 0xa1.toByte(), 0xac.toByte(), 0x8f.toByte(), 0x82.toByte(), 0x95.toByte(), 0x98.toByte(), 0xd3.toByte(), 0xde.toByte(), 0xc9.toByte(), 0xc4.toByte(), 0xe7.toByte(), 0xea.toByte(), 0xfd.toByte(), 0xf0.toByte()),
        byteArrayOf(0x6b.toByte(), 0x66.toByte(), 0x71.toByte(), 0x7c.toByte(), 0x5f.toByte(), 0x52.toByte(), 0x45.toByte(), 0x48.toByte(), 0x03.toByte(), 0x0e.toByte(), 0x19.toByte(), 0x14.toByte(), 0x37.toByte(), 0x3a.toByte(), 0x2d.toByte(), 0x20.toByte()),
        byteArrayOf(0x6d.toByte(), 0x60.toByte(), 0x77.toByte(), 0x7a.toByte(), 0x59.toByte(), 0x54.toByte(), 0x43.toByte(), 0x4e.toByte(), 0x05.toByte(), 0x08.toByte(), 0x1f.toByte(), 0x12.toByte(), 0x31.toByte(), 0x3c.toByte(), 0x2b.toByte(), 0x26.toByte()),
        byteArrayOf(0xbd.toByte(), 0xb0.toByte(), 0xa7.toByte(), 0xaa.toByte(), 0x89.toByte(), 0x84.toByte(), 0x93.toByte(), 0x9e.toByte(), 0xd5.toByte(), 0xd8.toByte(), 0xcf.toByte(), 0xc2.toByte(), 0xe1.toByte(), 0xec.toByte(), 0xfb.toByte(), 0xf6.toByte()),
        byteArrayOf(0xd6.toByte(), 0xdb.toByte(), 0xcc.toByte(), 0xc1.toByte(), 0xe2.toByte(), 0xef.toByte(), 0xf8.toByte(), 0xf5.toByte(), 0xbe.toByte(), 0xb3.toByte(), 0xa4.toByte(), 0xa9.toByte(), 0x8a.toByte(), 0x87.toByte(), 0x90.toByte(), 0x9d.toByte()),
        byteArrayOf(0x06.toByte(), 0x0b.toByte(), 0x1c.toByte(), 0x11.toByte(), 0x32.toByte(), 0x3f.toByte(), 0x28.toByte(), 0x25.toByte(), 0x6e.toByte(), 0x63.toByte(), 0x74.toByte(), 0x79.toByte(), 0x5a.toByte(), 0x57.toByte(), 0x40.toByte(), 0x4d.toByte()),
        byteArrayOf(0xda.toByte(), 0xd7.toByte(), 0xc0.toByte(), 0xcd.toByte(), 0xee.toByte(), 0xe3.toByte(), 0xf4.toByte(), 0xf9.toByte(), 0xb2.toByte(), 0xbf.toByte(), 0xa8.toByte(), 0xa5.toByte(), 0x86.toByte(), 0x8b.toByte(), 0x9c.toByte(), 0x91.toByte()),
        byteArrayOf(0x0a.toByte(), 0x07.toByte(), 0x10.toByte(), 0x1d.toByte(), 0x3e.toByte(), 0x33.toByte(), 0x24.toByte(), 0x29.toByte(), 0x62.toByte(), 0x6f.toByte(), 0x78.toByte(), 0x75.toByte(), 0x56.toByte(), 0x5b.toByte(), 0x4c.toByte(), 0x41.toByte()),
        byteArrayOf(0x61.toByte(), 0x6c.toByte(), 0x7b.toByte(), 0x76.toByte(), 0x55.toByte(), 0x58.toByte(), 0x4f.toByte(), 0x42.toByte(), 0x09.toByte(), 0x04.toByte(), 0x13.toByte(), 0x1e.toByte(), 0x3d.toByte(), 0x30.toByte(), 0x27.toByte(), 0x2a.toByte()),
        byteArrayOf(0xb1.toByte(), 0xbc.toByte(), 0xab.toByte(), 0xa6.toByte(), 0x85.toByte(), 0x88.toByte(), 0x9f.toByte(), 0x92.toByte(), 0xd9.toByte(), 0xd4.toByte(), 0xc3.toByte(), 0xce.toByte(), 0xed.toByte(), 0xe0.toByte(), 0xf7.toByte(), 0xfa.toByte()),
        byteArrayOf(0xb7.toByte(), 0xba.toByte(), 0xad.toByte(), 0xa0.toByte(), 0x83.toByte(), 0x8e.toByte(), 0x99.toByte(), 0x94.toByte(), 0xdf.toByte(), 0xd2.toByte(), 0xc5.toByte(), 0xc8.toByte(), 0xeb.toByte(), 0xe6.toByte(), 0xf1.toByte(), 0xfc.toByte()),
        byteArrayOf(0x67.toByte(), 0x6a.toByte(), 0x7d.toByte(), 0x70.toByte(), 0x53.toByte(), 0x5e.toByte(), 0x49.toByte(), 0x44.toByte(), 0x0f.toByte(), 0x02.toByte(), 0x15.toByte(), 0x18.toByte(), 0x3b.toByte(), 0x36.toByte(), 0x21.toByte(), 0x2c.toByte()),
        byteArrayOf(0x0c.toByte(), 0x01.toByte(), 0x16.toByte(), 0x1b.toByte(), 0x38.toByte(), 0x35.toByte(), 0x22.toByte(), 0x2f.toByte(), 0x64.toByte(), 0x69.toByte(), 0x7e.toByte(), 0x73.toByte(), 0x50.toByte(), 0x5d.toByte(), 0x4a.toByte(), 0x47.toByte()),
        byteArrayOf(0xdc.toByte(), 0xd1.toByte(), 0xc6.toByte(), 0xcb.toByte(), 0xe8.toByte(), 0xe5.toByte(), 0xf2.toByte(), 0xff.toByte(), 0xb4.toByte(), 0xb9.toByte(), 0xae.toByte(), 0xa3.toByte(), 0x80.toByte(), 0x8d.toByte(), 0x9a.toByte(), 0x97.toByte()),
)

private val m14 = arrayOf(
        byteArrayOf(0x00.toByte(), 0x0e.toByte(), 0x1c.toByte(), 0x12.toByte(), 0x38.toByte(), 0x36.toByte(), 0x24.toByte(), 0x2a.toByte(), 0x70.toByte(), 0x7e.toByte(), 0x6c.toByte(), 0x62.toByte(), 0x48.toByte(), 0x46.toByte(), 0x54.toByte(), 0x5a.toByte()),
        byteArrayOf(0xe0.toByte(), 0xee.toByte(), 0xfc.toByte(), 0xf2.toByte(), 0xd8.toByte(), 0xd6.toByte(), 0xc4.toByte(), 0xca.toByte(), 0x90.toByte(), 0x9e.toByte(), 0x8c.toByte(), 0x82.toByte(), 0xa8.toByte(), 0xa6.toByte(), 0xb4.toByte(), 0xba.toByte()),
        byteArrayOf(0xdb.toByte(), 0xd5.toByte(), 0xc7.toByte(), 0xc9.toByte(), 0xe3.toByte(), 0xed.toByte(), 0xff.toByte(), 0xf1.toByte(), 0xab.toByte(), 0xa5.toByte(), 0xb7.toByte(), 0xb9.toByte(), 0x93.toByte(), 0x9d.toByte(), 0x8f.toByte(), 0x81.toByte()),
        byteArrayOf(0x3b.toByte(), 0x35.toByte(), 0x27.toByte(), 0x29.toByte(), 0x03.toByte(), 0x0d.toByte(), 0x1f.toByte(), 0x11.toByte(), 0x4b.toByte(), 0x45.toByte(), 0x57.toByte(), 0x59.toByte(), 0x73.toByte(), 0x7d.toByte(), 0x6f.toByte(), 0x61.toByte()),
        byteArrayOf(0xad.toByte(), 0xa3.toByte(), 0xb1.toByte(), 0xbf.toByte(), 0x95.toByte(), 0x9b.toByte(), 0x89.toByte(), 0x87.toByte(), 0xdd.toByte(), 0xd3.toByte(), 0xc1.toByte(), 0xcf.toByte(), 0xe5.toByte(), 0xeb.toByte(), 0xf9.toByte(), 0xf7.toByte()),
        byteArrayOf(0x4d.toByte(), 0x43.toByte(), 0x51.toByte(), 0x5f.toByte(), 0x75.toByte(), 0x7b.toByte(), 0x69.toByte(), 0x67.toByte(), 0x3d.toByte(), 0x33.toByte(), 0x21.toByte(), 0x2f.toByte(), 0x05.toByte(), 0x0b.toByte(), 0x19.toByte(), 0x17.toByte()),
        byteArrayOf(0x76.toByte(), 0x78.toByte(), 0x6a.toByte(), 0x64.toByte(), 0x4e.toByte(), 0x40.toByte(), 0x52.toByte(), 0x5c.toByte(), 0x06.toByte(), 0x08.toByte(), 0x1a.toByte(), 0x14.toByte(), 0x3e.toByte(), 0x30.toByte(), 0x22.toByte(), 0x2c.toByte()),
        byteArrayOf(0x96.toByte(), 0x98.toByte(), 0x8a.toByte(), 0x84.toByte(), 0xae.toByte(), 0xa0.toByte(), 0xb2.toByte(), 0xbc.toByte(), 0xe6.toByte(), 0xe8.toByte(), 0xfa.toByte(), 0xf4.toByte(), 0xde.toByte(), 0xd0.toByte(), 0xc2.toByte(), 0xcc.toByte()),
        byteArrayOf(0x41.toByte(), 0x4f.toByte(), 0x5d.toByte(), 0x53.toByte(), 0x79.toByte(), 0x77.toByte(), 0x65.toByte(), 0x6b.toByte(), 0x31.toByte(), 0x3f.toByte(), 0x2d.toByte(), 0x23.toByte(), 0x09.toByte(), 0x07.toByte(), 0x15.toByte(), 0x1b.toByte()),
        byteArrayOf(0xa1.toByte(), 0xaf.toByte(), 0xbd.toByte(), 0xb3.toByte(), 0x99.toByte(), 0x97.toByte(), 0x85.toByte(), 0x8b.toByte(), 0xd1.toByte(), 0xdf.toByte(), 0xcd.toByte(), 0xc3.toByte(), 0xe9.toByte(), 0xe7.toByte(), 0xf5.toByte(), 0xfb.toByte()),
        byteArrayOf(0x9a.toByte(), 0x94.toByte(), 0x86.toByte(), 0x88.toByte(), 0xa2.toByte(), 0xac.toByte(), 0xbe.toByte(), 0xb0.toByte(), 0xea.toByte(), 0xe4.toByte(), 0xf6.toByte(), 0xf8.toByte(), 0xd2.toByte(), 0xdc.toByte(), 0xce.toByte(), 0xc0.toByte()),
        byteArrayOf(0x7a.toByte(), 0x74.toByte(), 0x66.toByte(), 0x68.toByte(), 0x42.toByte(), 0x4c.toByte(), 0x5e.toByte(), 0x50.toByte(), 0x0a.toByte(), 0x04.toByte(), 0x16.toByte(), 0x18.toByte(), 0x32.toByte(), 0x3c.toByte(), 0x2e.toByte(), 0x20.toByte()),
        byteArrayOf(0xec.toByte(), 0xe2.toByte(), 0xf0.toByte(), 0xfe.toByte(), 0xd4.toByte(), 0xda.toByte(), 0xc8.toByte(), 0xc6.toByte(), 0x9c.toByte(), 0x92.toByte(), 0x80.toByte(), 0x8e.toByte(), 0xa4.toByte(), 0xaa.toByte(), 0xb8.toByte(), 0xb6.toByte()),
        byteArrayOf(0x0c.toByte(), 0x02.toByte(), 0x10.toByte(), 0x1e.toByte(), 0x34.toByte(), 0x3a.toByte(), 0x28.toByte(), 0x26.toByte(), 0x7c.toByte(), 0x72.toByte(), 0x60.toByte(), 0x6e.toByte(), 0x44.toByte(), 0x4a.toByte(), 0x58.toByte(), 0x56.toByte()),
        byteArrayOf(0x37.toByte(), 0x39.toByte(), 0x2b.toByte(), 0x25.toByte(), 0x0f.toByte(), 0x01.toByte(), 0x13.toByte(), 0x1d.toByte(), 0x47.toByte(), 0x49.toByte(), 0x5b.toByte(), 0x55.toByte(), 0x7f.toByte(), 0x71.toByte(), 0x63.toByte(), 0x6d.toByte()),
        byteArrayOf(0xd7.toByte(), 0xd9.toByte(), 0xcb.toByte(), 0xc5.toByte(), 0xef.toByte(), 0xe1.toByte(), 0xf3.toByte(), 0xfd.toByte(), 0xa7.toByte(), 0xa9.toByte(), 0xbb.toByte(), 0xb5.toByte(), 0x9f.toByte(), 0x91.toByte(), 0x83.toByte(), 0x8d.toByte()),
)

private val rCon = arrayOf(
        byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x08.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x10.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x20.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x40.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x1b.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        byteArrayOf(0x36.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
)

class Block(source: ByteArray, key: ByteArray) {
    private var block: MutableList<ByteArray> = mutableListOf() // Блок байт
    private var secretKey: MutableList<ByteArray> = mutableListOf() // Ключ
    private var roundKey: MutableList<ByteArray> = mutableListOf() // Ключ для i-го раунда
    private var extendedKey: MutableList<ByteArray> = mutableListOf() // Расширенный ключ
    private var blockSize: Int // Размер блока

    init {
        // Дополнение массива байт до развмера блока, если не полный, и запонение блока и ключа
        val fullSource = ByteArray(4 * Nb)
        blockSize = source.size
        source.copyInto(fullSource)
        if (blockSize > 4 * Nb) {
            print("Source size = $blockSize")
            exitProcess(5)
        }
        if (key.size != Nk * 4) {
            print("Key size = ${key.size}")
            exitProcess(1)
        }
        repeat(4) {
            block.add(ByteArray(fullSource.size / 4))
        }
        repeat(4) {
            secretKey.add(ByteArray(key.size / 4))
        }
        repeat(4) {
            roundKey.add(ByteArray(Nb))
        }
        for (i in source.indices) {
            block[i % 4][i / 4] = fullSource[i]
        }
        for (i in key.indices) {
            secretKey[i % 4][i / 4] = key[i]
        }
    }

    // Вывод в файл только не нулевых байт
    private fun printBlock(output: File) {
        val k = ByteArray(block[0].size * block.size)
        loop@ for (i in block[0].indices) {
            for (j in block.indices) {
                if (block[j][i] == 0x00.toByte()) {
                    blockSize = block.size * i + j
                    break@loop
                }
                k[block.size * i + j] = block[j][i]
            }
        }
        output.appendBytes(k.sliceArray(IntRange(0, blockSize - 1)))
    }

    // Вывод блока в файл
    private fun printBlockFull(output: File) {
        val k = ByteArray(block[0].size * block.size)
        for (i in block[0].indices) {
            for (j in block.indices) {
                k[block.size * i + j] = block[j][i]
            }
        }
        output.appendBytes(k)
    }

    // Замена байт через таблицу S-box
    private fun byteSub() {
        for (i in 0 until block.size) {
            for (j in 1 until block[i].size) {
                block[i][j] = search(block[i][j], 0)
            }
        }
    }

    // Обратная замена байт через таблицк invS-box
    private fun invByteSub() {
        for (i in 0 until block.size) {
            for (j in 1 until block[i].size) {
                block[i][j] = search(block[i][j], 1)
            }
        }
    }

    // Сдвиг массива байт влево на 1 байт
    private fun leftShift(arr: ByteArray) {
        val tmp = arr[0]
        for (i in 0 until arr.size - 1) {
            arr[i] = arr[i + 1]
        }
        arr[arr.size - 1] = tmp
    }

    // Сдвиг массива байт вправо на 1 байт
    private fun rightShift(arr: ByteArray) {
        val tmp = arr[arr.size - 1]
        for (i in arr.size - 1 downTo 1) {
            arr[i] = arr[i - 1]
        }
        arr[0] = tmp
    }

    // Сдвиг строк блока
    private fun shiftRow() {
        for (i in 1 until block.size) {
            for (j in 0 until i) {
                leftShift(block[i])
            }
        }
    }

    // Обратное перобразование сдвига строк блока
    private fun invShiftRow() {
        for (i in 1 until block.size) {
            for (j in 0 until i) {
                rightShift(block[i])
            }
        }
    }

    // Операция смешивания блока с полиномом использую обратную линейную трансформацию
    private fun mixColumns() {
        for (i in 0 until block[0].size) {
            val a: List<Byte> = listOf(block[0][i], block[1][i], block[2][i], block[3][i])
            block[0][i] = search(a[0], 2).xor(search(a[1], 3)).xor(a[2]).xor(a[3])
            block[1][i] = a[0].xor(search(a[1], 2)).xor(search(a[2], 3)).xor(a[3])
            block[2][i] = a[0].xor(a[1]).xor(search(a[2], 2)).xor(search(a[3], 3))
            block[3][i] = search(a[0], 3).xor(a[1]).xor(a[2]).xor(search(a[3], 2))
        }
    }

    // Обратное преобразование перемещивания стобцов
    private fun invMixColumns() {
        for (i in 0 until block[0].size) {
            val a: List<Byte> = listOf(block[0][i], block[1][i], block[2][i], block[3][i])
            block[0][i] = search(a[0], 14).xor(search(a[1], 11)).xor(search(a[2], 13)).xor(search(a[3], 9))
            block[1][i] = search(a[0], 9).xor(search(a[1], 14)).xor(search(a[2], 11)).xor(search(a[3], 13))
            block[2][i] = search(a[0], 13).xor(search(a[1], 9)).xor(search(a[2], 14)).xor(search(a[3], 11))
            block[3][i] = search(a[0], 11).xor(search(a[1], 13)).xor(search(a[2], 9)).xor(search(a[3], 14))
        }
    }

    // Применение сложение по модулю блока с ключом раунда
    private fun addRoundKey(n: Int) {
        for (i in 0 until Nb) {
            for (j in 0 until roundKey.size) {
                roundKey[j][i] = extendedKey[j][Nb * n + i]
            }
        }
        for (i in block.indices) {
            for (j in block[0].indices) {
                block[i][j] = block[i][j].xor(roundKey[i][j])
            }
        }
    }

    // Замена через таблицу констант
    private fun search(b: Byte, n: Int): Byte {
        return when (n) {
            0 -> SBox[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            1 -> InvSBox[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            2 -> m2[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            3 -> m3[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            9 -> m9[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            11 -> m11[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            13 -> m13[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            14 -> m14[b.toUByte().div(16u).toInt()][b.toUByte().rem(16u).toInt()]
            else -> {
                0x00
            }
        }
    }

    // сдвиг слова по на один байт влево
    private fun rotWord(n: Int) {
        val tmp = extendedKey[0][n]
        for (i in 0 until extendedKey.size - 1) {
            extendedKey[i][n] = extendedKey[i + 1][n]
        }
        extendedKey[extendedKey.size - 1][n] = tmp
    }

    // Замена байт ключа через таблицу S-box
    private fun byteSubWord(n: Int) {
        for (i in 0 until extendedKey.size) {
            extendedKey[i][n] = search(extendedKey[i][n], 0)
        }
    }

    // Генерация ключей раундов
    private fun keyExpansion() {
        repeat(4) {
            extendedKey.add(ByteArray(Nb * (Nr + 1)))
        }
        for (i in secretKey.indices) {
            for (j in 0 until Nk) {
                extendedKey[j][i] = secretKey[j][i]
            }
        }
        for (i in Nk until Nb * (Nr + 1)) {
            for (j in 0 until extendedKey.size) {
                extendedKey[j][i] = extendedKey[j][i - 1]
            }
            if (i % Nk == 0) {
                rotWord(i)
                byteSubWord(i)
                for (j in 0 until extendedKey.size) {
                    extendedKey[j][i] = extendedKey[j][i - Nk].xor(extendedKey[j][i]).xor(rCon[i / Nk - 1][j])
                }
            } else {
                for (j in 0 until extendedKey.size) {
                    extendedKey[j][i] = extendedKey[j][i - Nk].xor(extendedKey[j][i])
                }
            }
        }
    }

    // Применение режима CBC
    private fun applyCBC(previous: List<ByteArray>) {
        for (i in block.indices) {
            for (j in block[0].indices) {
                block[i][j] = block[i][j].xor(previous[i][j])
            }
        }
    }

    // Шифрация
    fun rijndael(output: File, previous: List<ByteArray>?): List<ByteArray> {
        keyExpansion()

        if (previous != null) {
            applyCBC(previous)
        }

        addRoundKey(0)
        for (i in 1 until Nr) {
            byteSub()
            shiftRow()
            mixColumns()
            addRoundKey(i)
        }
        byteSub()
        shiftRow()
        addRoundKey(Nr)
        printBlockFull(output)
        return block
    }

    // Дешифрация
    fun deRijndael(output: File, previous: List<ByteArray>?): List<ByteArray> {
        val copyBlock = block.stream().map { it.copyOf() }.collect(Collectors.toList())
        keyExpansion()
        addRoundKey(Nr)
        for (i in Nr - 1 downTo 1) {
            invShiftRow()
            invByteSub()
            addRoundKey(i)
            invMixColumns()
        }
        invShiftRow()
        invByteSub()
        addRoundKey(0)

        if (previous != null) {
            applyCBC(previous)
        }

        printBlock(output)
        return copyBlock
    }
}