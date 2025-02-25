import {
  TimelineConnector,
  TimelineDot,
  TimelineItem as MuiTimelineItem,
  TimelineSeparator,
} from "@mui/lab"
import TimelineIcon from "pages/PatientDashboard/components/Presentations/Timeline/TimelineIcon"
import EventIcon from "@mui/icons-material/Event"
import DeleteIcon from "@mui/icons-material/Delete"
import TimelineContent from "pages/PatientDashboard/components/Presentations/Timeline/TimelineContent"
import { Tooltip } from "@mui/material"
import { useState } from "react"

type Props = {
  last?: boolean
  item: { id: string; label: string }
  onDelete: (item: { id: string; label: string }) => void
}

export default function TimelineItem({ last = false, item, onDelete }: Props) {
  const [hover, setHover] = useState(false)
  return (
    <MuiTimelineItem>
      <TimelineSeparator>
        <TimelineIcon>
          <Tooltip title={hover ? "Entfernen" : undefined}>
            <TimelineDot
              onClick={() => {
                onDelete(item)
              }}
              onMouseEnter={() => setHover(true)}
              onMouseLeave={() => setHover(false)}
              style={{ cursor: "pointer" }}
            >
              {hover ? <DeleteIcon /> : <EventIcon />}
            </TimelineDot>
          </Tooltip>
        </TimelineIcon>
        <TimelineConnector
          sx={{
            height: "120px",
            backgroundColor: last ? "white" : undefined,
            position: "relative",
            zIndex: -1,
          }}
        />
      </TimelineSeparator>
      <TimelineContent>{item.label}</TimelineContent>
    </MuiTimelineItem>
  )
}
