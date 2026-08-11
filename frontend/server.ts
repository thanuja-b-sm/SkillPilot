import express from "express";
import path from "path";
import { createServer as createViteServer } from "vite";
import { GoogleGenAI } from "@google/genai";
import dotenv from "dotenv";

dotenv.config();


async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // Initialize Gemini AI client lazily/safely
  const getAiClient = () => {
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) return null;
    return new GoogleGenAI({
      apiKey,
      httpOptions: {
        headers: {
          "User-Agent": "aistudio-build",
        },
      },
    });
  };

  // API Route: Health check
  app.get("/api/health", (req, res) => {
    res.json({ status: "ok", app: "SkillPilot AI" });
  });

  // API Route: AI Explanation & Wording Enhancer
  // Strictly used to polish roadmap summaries and explanation wording, without changing scores or logic
  app.post("/api/ai/enhance-summary", async (req, res) => {
    try {
      const { careerTitle, currentMatchScore, keyStrengths, keyGaps, targetRoleGoal } = req.body;
      
      const ai = getAiClient();
      if (!ai) {
        // Graceful fallback if GEMINI_API_KEY is not configured yet
        return res.json({
          enhancedExplanation: `System-Calculated Match (${currentMatchScore}%): Based on your profile assessment, your technical alignment is strong in ${keyStrengths?.join(", ") || "core areas"}. Addressing ${keyGaps?.join(", ") || "identified gaps"} will accelerate your career transition into ${careerTitle}.`,
          source: "fallback-template"
        });
      }

      const prompt = `You are a career development assistant for SkillPilot.
Improve the wording and readability of this system-calculated career intelligence summary.
Career Target: ${careerTitle}
System Match Score: ${currentMatchScore}%
Key Strengths Identified by System: ${keyStrengths?.join(", ") || "N/A"}
Key Skill Gaps Identified by System: ${keyGaps?.join(", ") || "N/A"}
User Goal: ${targetRoleGoal || "Career Advancement"}

IMPORTANT:
1. Do NOT change or recalculate the match score (${currentMatchScore}%).
2. Frame your output purely as a supportive narrative explanation.
3. Keep it professional, encouraging, concise (2-3 short sentences), and well-structured with clear bullet points if helpful.`;

      const response = await ai.models.generateContent({
        model: "gemini-3.6-flash",
        contents: prompt,
        config: {
          systemInstruction: "You strictly enhance readability and phrasing for pre-calculated career analysis reports. Do not invent scores or alter system metrics.",
          temperature: 0.7,
        },
      });

      const text = response.text || "System analysis complete.";
      return res.json({
        enhancedExplanation: text,
        source: "gemini-ai"
      });
    } catch (err: any) {
      console.error("Error in AI summary enhancement:", err);
      return res.json({
        enhancedExplanation: "System analysis complete. SkillPilot calculated key milestones and gap priorities based on verified industry skill matrices.",
        source: "system-calculated"
      });
    }
  });

  // Forward all remaining /api calls to Spring Boot backend on http://localhost:8080
  app.use("/api", async (req, res) => {
    try {
      const targetUrl = `http://localhost:8080${req.originalUrl}`;
      const headers: Record<string, string> = {};
      
      if (req.headers["content-type"]) {
        headers["content-type"] = req.headers["content-type"] as string;
      }
      if (req.headers["authorization"]) {
        headers["authorization"] = req.headers["authorization"] as string;
      }

      const options: RequestInit = {
        method: req.method,
        headers,
      };

      if (["POST", "PUT", "PATCH"].includes(req.method) && req.body && Object.keys(req.body).length > 0) {
        options.body = JSON.stringify(req.body);
      }

      const backendRes = await fetch(targetUrl, options);
      res.status(backendRes.status);

      const contentType = backendRes.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        const json = await backendRes.json();
        return res.json(json);
      } else {
        const text = await backendRes.text();
        return res.send(text);
      }
    } catch (err: any) {
      if (err?.cause?.code === "ECONNREFUSED" || err?.code === "ECONNREFUSED") {
        console.warn(`[Proxy Warning] Spring Boot backend is offline on http://localhost:8080 (${req.method} ${req.originalUrl})`);
      } else {
        console.error("Error proxying request to Spring Boot backend:", err);
      }
      return res.status(502).json({
        error: "Bad Gateway",
        message: "Unable to connect to Spring Boot backend service on http://localhost:8080"
      });
    }
  });

  // Vite middleware for dev or static serving for prod
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*", (req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`SkillPilot server listening at http://0.0.0.0:${PORT}`);
  });
}

startServer();
