import { NextResponse } from "next/server";
import sharp from "sharp";

export async function GET() {
  // Create SVG with the bookmark icon
  const svgBuffer = Buffer.from(`
    <svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 512 512" fill="none">
      <rect width="512" height="512" rx="128" fill="url(#paint0_linear)" />
      <path d="M348 160H292C279.85 160 268.2 164.8 259.8 173.2C251.4 181.6 246.6 193.25 246.6 205.4V246.6H164V302.6H246.6V432H302.6V302.6H348L364 246.6H302.6V205.4C302.6 202.35 303.8 199.45 306 197.25C308.2 195.05 311.1 193.85 314.15 193.85H348V160Z" fill="white" stroke="white" stroke-width="16" stroke-linecap="round" stroke-linejoin="round" />
      <defs>
        <linearGradient id="paint0_linear" x1="0" y1="0" x2="512" y2="512" gradientUnits="userSpaceOnUse">
          <stop stop-color="#4F46E5" />
          <stop offset="1" stop-color="#8B5CF6" />
        </linearGradient>
      </defs>
    </svg>
  `);

  // Convert SVG to PNG with size 180x180 for Apple devices
  const pngBuffer = await sharp(svgBuffer).resize(180, 180).png().toBuffer();

  // Return the PNG image
  return new NextResponse(pngBuffer, {
    headers: {
      "Content-Type": "image/png",
      "Cache-Control": "public, max-age=31536000, immutable",
    },
  });
}
