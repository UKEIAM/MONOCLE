import { Timeline as MuiTimeline } from "@mui/lab"
import TimelineItem from "./TimelineItem"
import AddDate from "./AddDate"
import { reverse } from "./utils"
import { PresentationItem } from "../Presentations"

type Props = {
  items?: PresentationItem[]
  onAdd: () => void
  onDelete: (item: PresentationItem) => void
}

/**
 * Timeline
 * @todo add support for multiple timestamps again, or revise naming and interface to reflect behaviour
 */
export default function Timeline({ items = [], onAdd, onDelete }: Props) {
  const isEmpty = items.length === 0
  return (
    <div style={{ height: "90px", width: "100%" }}>
      <MuiTimeline
        sx={{
          width: "58px",
          transform: "rotate(90deg) translate(-20px, -100%)",
          transformOrigin: "0% 0",
        }}
      >
        {isEmpty ? <AddDate onAdd={onAdd} noItems={items.length === 0} /> : null}
        {reverse(items).map((item, index) => (
          <TimelineItem item={item} last={index === items.length - 1} onDelete={onDelete} />
        ))}
      </MuiTimeline>
    </div>
  )
}
