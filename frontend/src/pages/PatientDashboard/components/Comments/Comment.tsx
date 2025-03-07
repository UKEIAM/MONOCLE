import { Comment as CommentType } from "gen/api"
import {
  Avatar,
  Box,
  Grid,
  IconButton,
  ListItem,
  Tooltip,
  Typography,
  useTheme,
} from "@mui/material"
import PushPinIcon from "@mui/icons-material/PushPin"
import DeleteIcon from "@mui/icons-material/Delete"
import { useState } from "react"
import { useAuth } from "react-oidc-context"
import {
  nameToInitials,
  userProfileToFullname,
} from "pages/PatientDashboard/components/Comments/utils"

type Props = {
  comment: CommentType
  highlight?: boolean
  onToggleHighlight: (commentId: string) => void
  onDelete: (comment: any) => void
}

export default function Comment({
  comment,
  highlight = false,
  onToggleHighlight,
  onDelete,
}: Props) {
  const auth = useAuth()
  const theme = useTheme()
  const [hover, setHover] = useState(false)

  return (
    <ListItem onMouseEnter={() => setHover(true)} onMouseLeave={() => setHover(false)}>
      <Grid
        container
        direction={"row"}
        display={"flex"}
        style={{
          backgroundColor: hover ? theme.palette.primary.light : undefined,
          flexWrap: "nowrap",
        }}
      >
        <Grid item margin={"10px"}>
          <Avatar sx={{ backgroundColor: theme.palette.primary.main }}>
            {nameToInitials(comment.author!)}
          </Avatar>
        </Grid>
        <Grid item marginTop={"2px"} flexGrow={1}>
          <Grid container direction={"column"}>
            {highlight && (
              <Typography style={{ display: "flex", alignItems: "center", margin: "5px 0 5px 0" }}>
                <PushPinIcon style={{ color: "lightgrey" }} fontSize={"small"} />
                <Box component="span" color={"lightgray"}>
                  Hervorgehoben
                </Box>
              </Typography>
            )}
            <Grid
              item
              container
              direction={"row"}
              justifyContent={"space-between"}
              alignItems={"center"}
              height={"34px"}
            >
              <Typography>
                <Box component="span" fontWeight={"bold"}>
                  {comment.author}{" "}
                </Box>
                {/*<Box component="span" color={"lightgray"}>vor 2 Monaten</Box>*/}
              </Typography>
              <Grid item display={hover ? undefined : "none"} marginRight={"4px"}>
                <Tooltip title={comment.highlighted ? "Nicht mehr hervorheben" : "Hervorheben"}>
                  <IconButton size={"small"} onClick={() => onToggleHighlight(comment.id)}>
                    <PushPinIcon />
                  </IconButton>
                </Tooltip>
                {userProfileToFullname(auth.user?.profile!) === comment.author ? (
                  <Tooltip title={"Entfernen"}>
                    <IconButton size={"small"} onClick={() => onDelete(comment)}>
                      <DeleteIcon />
                    </IconButton>
                  </Tooltip>
                ) : undefined}
              </Grid>
            </Grid>
            <Typography maxWidth={"100%"} style={{ whiteSpace: "pre-wrap" }} component="pre">
              {comment.comment}
            </Typography>
          </Grid>
        </Grid>
      </Grid>
    </ListItem>
  )
}
