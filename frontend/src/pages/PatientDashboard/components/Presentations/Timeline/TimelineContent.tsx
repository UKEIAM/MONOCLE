import { TimelineContent as MuiTimelineContent } from "@mui/lab"
import React from "react"

type Props = {
  children: React.ReactNode
}

export default function TimelineContent({ children }: Props) {
  return (
    <MuiTimelineContent style={{ textAlign: "left", position: "relative", zIndex: -1 }}>
      <div
        style={{
          margin: "0 auto",
          position: "absolute",
          transform: "translateX(-25%) translateY(-50%) rotate(-90deg)",
        }}
      >
        {children}
      </div>
    </MuiTimelineContent>
  )
}
