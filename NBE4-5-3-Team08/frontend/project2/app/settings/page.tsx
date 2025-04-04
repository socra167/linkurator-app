"use client"

import { useState } from "react"
import Link from "next/link"
import { ArrowLeft, Moon, Sun, Monitor } from "lucide-react"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { useToast } from "@/components/ui/use-toast"

export default function SettingsPage() {
    const { toast } = useToast()
    const [theme, setTheme] = useState<"light" | "dark" | "system">("system")

    const handleThemeChange = (value: "light" | "dark" | "system") => {
        setTheme(value)

        // 테마 변경 로직
        if (value === "system") {
            const systemTheme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"
            document.documentElement.classList.toggle("dark", systemTheme === "dark")
        } else {
            document.documentElement.classList.toggle("dark", value === "dark")
        }

        // 로컬 스토리지에 테마 저장
        localStorage.setItem("theme", value)

        toast({
            title: "테마 변경됨",
            description: `테마가 ${value === "light" ? "라이트" : value === "dark" ? "다크" : "시스템"}모드로 변경되었습니다.`,
        })
    }

    return (
        <div className="container max-w-2xl mx-auto py-10 px-4">
            <div className="mb-6">
                <Link href="/" className="inline-flex items-center text-sm text-gray-500 hover:text-black">
                    <ArrowLeft className="mr-2 h-4 w-4" />
                    홈으로 돌아가기
                </Link>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle className="text-2xl">설정</CardTitle>
                    <CardDescription>앱 설정을 관리합니다.</CardDescription>
                </CardHeader>

                <CardContent className="space-y-6">
                    <div className="space-y-4">
                        <h3 className="text-lg font-medium">테마 설정</h3>

                        <RadioGroup
                            value={theme}
                            onValueChange={(value) => handleThemeChange(value as "light" | "dark" | "system")}
                        >
                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="light" id="light" />
                                <Label htmlFor="light" className="flex items-center">
                                    <Sun className="mr-2 h-4 w-4" />
                                    라이트 모드
                                </Label>
                            </div>

                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="dark" id="dark" />
                                <Label htmlFor="dark" className="flex items-center">
                                    <Moon className="mr-2 h-4 w-4" />
                                    다크 모드
                                </Label>
                            </div>

                            <div className="flex items-center space-x-2">
                                <RadioGroupItem value="system" id="system" />
                                <Label htmlFor="system" className="flex items-center">
                                    <Monitor className="mr-2 h-4 w-4" />
                                    시스템 설정 사용
                                </Label>
                            </div>
                        </RadioGroup>
                    </div>

                    <div className="space-y-4">
                        <h3 className="text-lg font-medium">알림 설정</h3>
                        <p className="text-sm text-muted-foreground">현재 알림 설정 기능은 개발 중입니다.</p>
                    </div>
                </CardContent>

                <CardFooter>
                    <p className="text-xs text-muted-foreground">설정은 자동으로 저장됩니다.</p>
                </CardFooter>
            </Card>
        </div>
    )
}

