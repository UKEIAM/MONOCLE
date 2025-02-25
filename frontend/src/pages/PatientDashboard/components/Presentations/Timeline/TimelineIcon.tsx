import React from "react"

type Props = {
  children: React.ReactNode
}

export default function TimelineIcon({ children }: Props) {
  return (
    <div style={{ position: "relative" }}>
      <div
        style={{
          margin: "0 auto",
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translateX(-50%) translateY(-50%) rotate(-90deg)",
        }}
      >
        {children}
      </div>
    </div>
  )
}
