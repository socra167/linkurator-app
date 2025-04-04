import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    domains: ["localhost"],
    remotePatterns: [
      {
        protocol: "https",
        hostname: "**", // 모든 도메인 허용
      },
    ],
  },
};

export default nextConfig;
