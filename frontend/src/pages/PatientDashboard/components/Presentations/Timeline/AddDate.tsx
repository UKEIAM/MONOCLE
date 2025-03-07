import { TimelineConnector, TimelineDot, TimelineItem, TimelineSeparator } from "@mui/lab"
import TimelineIcon from "./TimelineIcon"
import TimelineContent from "./TimelineContent"
import { Tooltip } from "@mui/material"
import AddIcon from "@mui/icons-material/Add"

type Props = {
  noItems?: boolean
  onAdd: () => void
}

export default function AddDate({ noItems = false, onAdd }: Props) {
  return (
    <TimelineItem>
      <TimelineSeparator>
        <TimelineIcon>
          <Tooltip title={"Vorstellungsdatum hinzufügen"}>
            <TimelineDot onClick={onAdd} style={{ cursor: "pointer" }}>
              <AddIcon />
            </TimelineDot>
          </Tooltip>
        </TimelineIcon>
        <TimelineConnector
          sx={{
            height: "120px",
            backgroundColor: noItems ? "white" : undefined,
            position: "relative",
            zIndex: -1,
          }}
        />
      </TimelineSeparator>
      <TimelineContent>{undefined /*TimelineContent required for styling*/}</TimelineContent>
    </TimelineItem>
  )
}
